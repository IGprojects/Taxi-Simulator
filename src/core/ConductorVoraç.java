package core;

import java.time.LocalTime;
import java.util.List;

import events.CarregarBateriaEvent;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;

/**
 * @class ConductorVoraç
 * @brief Defineix un tipus de conductor (conductor voraç)
 *
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class ConductorVoraç extends Conductor {

    public ConductorVoraç(int id, String nom, Vehicle vehicle) {
        super(id, nom, vehicle);
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

    public Ruta planificarRuta(Peticio peticio, Mapa mapa) {
        List<Lloc> cami = mapa.camiVoraç(peticio.obtenirOrigen(), peticio.obtenirDesti());
        double distanciaTotal = 0;
        double tempsTotal = 0;

        for (int i = 0; i < cami.size() - 1; i++) {
            Lloc origen = cami.get(i);
            Lloc desti = cami.get(i + 1);
            distanciaTotal += mapa.calcularDistancia(origen, desti);
            tempsTotal += mapa.calcularTemps(origen, desti);
        }
        return new Ruta(cami, peticio.obtenirHoraMinimaRecollida(), distanciaTotal, tempsTotal, this, false);
    }

    @Override
    public boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual) {
        // Comprovar si el vehicle pot fer la petició
        if (vehicle.teBateria(distancia, true)) {
            return true;
        } else {
            Ruta r = mapa.rutaParquingMesProper(vehicle.getUbicacioActual(), horaInici, this);

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

}

