package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import events.CarregarBateriaEvent;
import events.DeixarPassatgersEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import events.RecollirPassatgersEvent;

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
        List<Lloc> ultimCami = new ArrayList<>();
        List<Pair<Integer, Integer>> rutaLlocsOrigenPeticions = new ArrayList<>();
        List<Pair<Integer, Integer>> rutaLlocsDestiPeticions = new ArrayList<>();
        Vehicle vehicle = getVehicle();
        Lloc ubicacio = vehicle.getUbicacioActual();
        LocalTime horaActual = horaIniciSimulacio;
        Peticio primeraPeticio = null;
        double tempsFinsPrimerOrigen = 0;
        double autonomiaRestant = vehicle.obtenirBateria();
        double distTotal = 0;
        boolean rutaCarrega = false;
        rutaLlocs.add(ubicacio);
        int passatgersActuals = vehicle.getPassatgersActuals();
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
                Ruta rutaFinsCarregador = s.getMapa().rutaParquingPrivatMesProper(p.obtenirDesti(),
                        p.obtenirHoraMaximaArribada(), this);
                if (cami == null || cami.isEmpty() || rutaFinsCarregador == null
                        || rutaFinsCarregador.getLlocs().isEmpty()) {
                    System.out.println("Petició " + p.obtenirId()
                            + " descartada: no hi ha camí a origen o no hi ha camí a carregador.");
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

                ultimCami = s.getMapa().camiVoraç(p.obtenirOrigen(), p.obtenirDesti());
                if (ultimCami == null || ultimCami.isEmpty()) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: no hi ha camí a destí.");
                    continue;
                }

                double tempsDesti = tempsOrigen + s.getMapa().calcularTempsRuta(ultimCami);
                double distDesti = distOrigen + s.getMapa().calcularDistanciaRuta(ultimCami);

                LocalTime horaFinal = horaActual.plusMinutes((long) tempsDesti);
                if (horaFinal.isAfter(p.obtenirHoraMaximaArribada())) {
                    System.out.println("Petició " + p.obtenirId() + " descartada: arribada massa tard a destí.");
                    continue;
                }

                if (passatgersActuals + p.obtenirNumPassatgers() <= vehicle.MAXPASSATGERS) {
                    if (tempsDesti < millorTemps) {
                        millorTemps = tempsDesti;
                        millorPeticio = p;
                        millorCami = new ArrayList<>();
                        millorCami.addAll(cami);
                        millorCami.addAll(ultimCami.subList(1, ultimCami.size()));
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

            // Comprovar si la bateria és suficient per fer la ruta
            double distanciaRuta = s.getMapa().calcularDistanciaRuta(millorCami);
            int consumPercentatge = (int) Math.ceil((distanciaRuta / vehicle.AUTONOMIA) * 100);

            Ruta rutaFinsCarregador = s.getMapa().rutaParquingPrivatMesProper(millorPeticio.obtenirDesti(),
                    millorPeticio.obtenirHoraMaximaArribada(), this);
            for (Lloc l : rutaFinsCarregador.getLlocs()) {
                System.out.println("El vehicle " + vehicle.getId() + " ha arribat al parquing "
                        + l.obtenirId() + ".");

            }
            double distanciaFinsCarregador = s.getMapa().calcularDistanciaRuta(rutaFinsCarregador.getLlocs());
            int consumFinsCarregador = (int) Math.ceil((distanciaFinsCarregador / vehicle.AUTONOMIA) * 100);

            // Comprovar si la bateria és suficient per arribar al carregador
            if ((autonomiaRestant - consumPercentatge) - consumFinsCarregador < 0) {
                rutaCarrega = true;
                rutaFinsCarregador.getLlocs().remove(0);
                millorCami.removeAll(ultimCami);
                millorCami.addAll(rutaFinsCarregador.getLlocs());
            } else { // S'afegeix la peticio
                System.out.println("Afegida petició " + millorPeticio.obtenirId() + " a la ruta.");
                rutaLlocsOrigenPeticions.add(new Pair<>(millorPeticio.obtenirOrigen().obtenirId(),
                        millorPeticio.obtenirNumPassatgers()));
                rutaLlocsDestiPeticions.add(new Pair<>(millorPeticio.obtenirDesti().obtenirId(),
                        millorPeticio.obtenirNumPassatgers()));
                recollides.add(millorPeticio);
                millorPeticio.peticioEnProces();
                passatgersActuals += millorPeticio.obtenirNumPassatgers();
            }

            for (Lloc l : millorCami.subList(1, millorCami.size())) {
                rutaLlocs.add(l);
            }

            autonomiaRestant -= consumPercentatge;
            horaActual = horaActual.plusMinutes((long) millorTemps);
            ubicacio = rutaLlocs.get(rutaLlocs.size() - 1);
        }

        if (rutaLlocs.size() == 1) {
            System.out.println("Cap ruta planificada.");
            return null;
        }
        // rutaLlocs.addAll(rutaFinsCarregador.getLlocs());
        peticions.removeAll(recollides);

        LocalTime horaSortidaReal = primeraPeticio.obtenirHoraMinimaRecollida()
                .minusMinutes((long) tempsFinsPrimerOrigen);
        System.out.println("Ruta final planificada amb " + recollides.size() + " peticions. Hora de sortida real: "
                + horaSortidaReal);

        Ruta rutaCompleta = new Ruta(rutaLlocs, horaSortidaReal, distTotal, s.getMapa().calcularTempsRuta(rutaLlocs),
                this,
                rutaCarrega);
        rutaCompleta.assignarLlocsOrigenPeticions(rutaLlocsOrigenPeticions);
        rutaCompleta.assignarLlocsDestiPeticions(rutaLlocsDestiPeticions);

        return rutaCompleta;
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

            int passatgersOrigen = ruta.trobarOrigenId(origen.obtenirId());
            if (passatgersOrigen != -1) {
                RecollirPassatgersEvent recollir = new RecollirPassatgersEvent(horaActual, this, origen,
                        passatgersOrigen);
                simulador.afegirEsdeveniment(recollir);
            }

            MoureVehicleEvent moure = new MoureVehicleEvent(horaActual, vehicle, origen, desti, distancia);
            simulador.afegirEsdeveniment(moure);

            int passatgersDesti = ruta.trobarDestiId(desti.obtenirId());
            if (passatgersDesti != -1) {
                DeixarPassatgersEvent recollir = new DeixarPassatgersEvent(horaActual, this, origen,
                        passatgersDesti);
                simulador.afegirEsdeveniment(recollir);
            }

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

    public int getIdParquing() {
        return parquingPrivat.obtenirId();
    }
}