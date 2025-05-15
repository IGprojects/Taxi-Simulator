package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import events.CarregarBateriaEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;

/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class ConductorPlanificador extends Conductor {

    private final Vehicle vehicle; /// < Vehicle que condueix el conductor.
    private Parquing parquingPrivat; /// < Pàrquing privat assignat al conductor.

    public ConductorPlanificador(int id, String nom, Vehicle vehicle, Parquing parquingPrivat) {
        super(id, nom, vehicle);
        this.id = id;
        this.nom = nom;
        this.vehicle = vehicle;
        this.parquingPrivat = parquingPrivat;
    }


    public Ruta planificarRuta(List<Peticio> peticions, Simulador s, LocalTime horaIniciSimulacio) {
        List<Peticio> recollides = new ArrayList<>();
        List<Lloc> rutaLlocs = new ArrayList<>();
        Vehicle vehicle = getVehicle();
        Lloc ubicacio = vehicle.getUbicacioActual();
        LocalTime horaActual = horaIniciSimulacio;
        Peticio primeraPeticio = null;
        double tempsFinsPrimerOrigen = 0;
        double autonomiaRestant = vehicle.obtenirBateria();
        double distTotal = 0;
        rutaLlocs.add(ubicacio);

        System.out.println("Inici planificació ruta del conductor " + this.getId() + " a les " + horaIniciSimulacio);
        System.out.println(
                "Ubicació inicial: " + ubicacio.obtenirId() + ", bateria: " + autonomiaRestant + " passatgers: "
                        + vehicle.getPassatgersActuals());

        bucle: while (true) {
            Peticio millorPeticio = null;
            List<Lloc> millorCami = null;
            double millorTemps = Double.MAX_VALUE;
            if (primeraPeticio != null && !primeraPeticio.esVehicleCompartit()) {
                break;
            }
            for (Peticio p : peticions) {
                if (p.estatActual() != EstatPeticio.PENDENT) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: no està pendent.");
                    continue;
                }

                List<Lloc> cami = s.getMapa().camiVoraç(ubicacio, p.obtenirOrigen());
                if (cami == null || cami.isEmpty()) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: no hi ha camí a origen.");
                    continue;
                }

                double tempsOrigen = s.getMapa().calcularTempsRuta(cami);
                double distOrigen = s.getMapa().calcularDistanciaRuta(cami);

                LocalTime horaArribada = horaActual.plusMinutes((long) tempsOrigen);
                if (horaArribada.isAfter(p.obtenirHoraMaximaArribada())) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: arribada massa tard a origen.");
                    continue;
                }
                if (autonomiaRestant < distOrigen) {
                    System.out.println(
                            "Petició " + p.obtenirId() + " descartada: no hi ha bateria per arribar a origen.");
                    continue;
                }

                List<Lloc> camiFinsDesti = s.getMapa().camiVoraç(p.obtenirOrigen(), p.obtenirDesti());
                if (camiFinsDesti == null || camiFinsDesti.isEmpty()) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: no hi ha camí a destí.");
                    continue;
                }

                double tempsDesti = tempsOrigen + s.getMapa().calcularTempsRuta(camiFinsDesti);
                double distDesti = distOrigen + s.getMapa().calcularDistanciaRuta(camiFinsDesti);

                LocalTime horaFinal = horaActual.plusMinutes((long) tempsDesti);
                if (horaFinal.isAfter(p.obtenirHoraMaximaArribada())) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: arribada massa tard a destí.");
                    continue;
                }
                if (!teBateria(distOrigen, s, s.getMapa(), horaActual, horaActual)) {
                    System.out
                            .println("Petició " + p.obtenirId() + " descartada: no hi ha bateria per arribar a destí.");
                    break bucle;
                }

                if (vehicle.passatgersActuals() + p.obtenirNumPassatgers() <= vehicle.MAXPASSATGERS) {
                    if (tempsDesti < millorTemps) {
                        millorTemps = tempsDesti;
                        millorPeticio = p;
                        millorCami = new ArrayList<>();
                        millorCami.addAll(cami);
                        millorCami.addAll(camiFinsDesti.subList(1, camiFinsDesti.size()));
                        distTotal = distOrigen + distDesti;
                        // Només guardem si és la primera
                        if (primeraPeticio == null) {
                            primeraPeticio = p;
                            tempsFinsPrimerOrigen = s.getMapa().calcularTempsRuta(cami);
                        }
                    }
                } else {
                    System.out.println("Petició " + p.obtenirId()
                            + " descartada: no compleix restriccions de passatgers.");
                }
            }

            if (millorPeticio == null) {
                System.out.println("No s'ha pogut afegir més peticions.");
                break;
            }

            System.out.println("Afegida petició " + millorPeticio.obtenirId() + " a la ruta.");
            recollides.add(millorPeticio);
            millorPeticio.peticioEnProces();
            vehicle.afegirPassatgers(millorPeticio.obtenirNumPassatgers());

            for (Lloc l : millorCami.subList(1, millorCami.size())) {
                rutaLlocs.add(l);
            }

            double consum = s.getMapa().calcularDistanciaRuta(millorCami);
            autonomiaRestant -= consum;
            horaActual = horaActual.plusMinutes((long) millorTemps);
            ubicacio = rutaLlocs.get(rutaLlocs.size() - 1);
        }

        if (rutaLlocs.size() == 1) {
            System.out.println("Cap ruta planificada.");
            return null;
        }

        peticions.removeAll(recollides);

        LocalTime horaSortidaReal = primeraPeticio.obtenirHoraMinimaRecollida()
                .minusMinutes((long) tempsFinsPrimerOrigen);
        System.out.println("Ruta final planificada amb " + recollides.size() + " peticions. Hora de sortida real: "
                + horaSortidaReal);

        return new Ruta(rutaLlocs, horaSortidaReal, distTotal, s.getMapa().calcularTempsRuta(rutaLlocs), this,
                false);
    }

    public boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual) {
        // Comprovar si el vehicle pot fer la petició
        if (vehicle.teBateria(distancia, false)) {
            return true;
        } else {
            Ruta r = mapa.rutaParquingPrivatMesProper(vehicle.getUbicacioActual(), horaInici, this);

            if (r != null) {
                ocupat = true;
                simulador.afegirEsdeveniment(new IniciRutaEvent(horaActual, this,
                        vehicle, r));
            }

            else
                System.out.println("No hi ha cap pàrquing disponible");

            return false;
        }
    }

    @Override
    public boolean potServirPeticio(int nombrePassatgers) {
        return !isOcupat() && !vehicle.esCarregant() && nombrePassatgers <= vehicle.getMaxPassatgers();
    }

    @Override
    public void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador) {
        List<Lloc> llocs = ruta.getLlocs();
        LocalTime horaActual = ruta.obtenirHoraInici();

        for (int i = 0; i < llocs.size() - 1; i++) {
            Lloc origen = llocs.get(i);
            Lloc desti = llocs.get(i + 1);

            double distancia = simulador.getMapa().calcularDistancia(origen, desti);
            double temps = simulador.getMapa().calcularTemps(origen, desti);

            horaActual = horaActual.plusMinutes((long) temps);

            MoureVehicleEvent moure = new MoureVehicleEvent(horaActual, vehicle, origen, desti, distancia);
            simulador.afegirEsdeveniment(moure);
        }
        if (ruta.isRutaCarrega()) {
            if (ruta.getLlocs().get(ruta.getLlocs().size() - 1) instanceof Parquing) {
                Parquing parquing = (Parquing) ruta.getLlocs().get(ruta.getLlocs().size() - 1);
                PuntCarrega pc = parquing.puntCarregaPublicDisponible();
                if (pc != null) {
                    System.out.println("El vehicle " + vehicle.getId() + " ha arribat al punt de càrrega "
                            + " del parquing " + parquing.obtenirId() + ".");
                    double duracio = 0;
                    duracio = pc.getTipusCarga() == TipusPuntCarrega.CARGA_LENTA ? vehicle.TEMPSCARGALENTA
                            : vehicle.TEMPSCARGARAPIDA;

                    LocalTime horaIniciCarrega = horaActual;
                    simulador.afegirEsdeveniment(new CarregarBateriaEvent(horaIniciCarrega, vehicle, duracio, this));

                } else {
                    System.out.println("No hi ha punts de càrrega disponibles.");
                }
            }

        } else {
            simulador.afegirEsdeveniment(new FiRutaEvent(horaActual, this, ruta));

        }
    }

    public Parquing getParquingPrivat() {
        return parquingPrivat;
    }
}