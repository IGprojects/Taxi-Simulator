package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class ConductorPlanificador extends Conductor {

    private final Vehicle vehicle;
    private final int id;
    private final String nom;
    private final List<Ruta> rutesPlanificades;
    private final List<Cami> tramsExecutats;

    public ConductorPlanificador(int id, String nom, Vehicle vehicle) {
        super(id, nom, vehicle);
        this.id = id;
        this.nom = nom;
        this.vehicle = vehicle;
        this.rutesPlanificades = new ArrayList<>();
        this.tramsExecutats = new ArrayList<>();
    }

    public void decidirMoviment(Mapa mapa, List<Peticio> peticions) {
        Lloc ubicacioActual = vehicle.getUbicacioActual();
        List<Peticio> pendents = new ArrayList<>(peticions);
        boolean haTrobat = false;

        while (!pendents.isEmpty()) {
            Peticio millor = seleccionarMillorPeticio(ubicacioActual, pendents, mapa);
            if (millor == null) break;

            Lloc origen = millor.obtenirOrigen();
            Lloc desti = millor.obtenirDesti();

            double distOrigen = mapa.calcularDistancia(ubicacioActual, origen);
            double distDesti = mapa.calcularDistancia(origen, desti);
            double distTotal = distOrigen + distDesti;
            double percentatgeDespres = vehicle.obtenirBateria() - ((distTotal / vehicle.AUTONOMIA) * 100);

            if (vehicle.teBateria(distTotal, false) && percentatgeDespres >= 20) {
                vehicle.moure(origen, distOrigen);
                vehicle.moure(desti, distDesti);

                if (origen.esRecollida()) vehicle.afegirPassatgers(millor.obtenirNumPassatgers());
                if (desti.esDeixada()) vehicle.alliberarPassatgers();

                ubicacioActual = desti;
                pendents.remove(millor);
                haTrobat = true;
            } else {
                pendents.remove(millor);
            }
        }

        if (!haTrobat) {
            Ruta rutaCarrega = mapa.rutaParquingMesProper(ubicacioActual, LocalTime.now(), this);
            if (rutaCarrega == null) {
                System.out.println("ATENCIÓ: No s'ha pogut trobar un parquing proper.");
                return;
            }
            for (Cami cami : rutaCarrega.obtenirTrams()) {
                vehicle.moure(cami.obtenirDesti(), cami.obtenirDistancia());
            }
            vehicle.carregarBateria(false);
        }
    }

    
    public Ruta planificarRuta(Mapa mapa, Set<Peticio> peticions) {
        Ruta ruta = new Ruta();
        ruta.setLlocs(new ArrayList<>());
        Lloc ubicacioActual = vehicle.getUbicacioActual();
        Set<Peticio> pendents = new HashSet<>(peticions);

        while (!pendents.isEmpty()) {
            Peticio millor = seleccionarMillorPeticio(ubicacioActual, new ArrayList<>(pendents), mapa);

            if (millor == null) {
                pendents.clear();
                continue;
            }

            Lloc origen = millor.obtenirOrigen();
            Lloc desti = millor.obtenirDesti();

            double distTotal = mapa.calcularDistancia(ubicacioActual, origen) +
                               mapa.calcularDistancia(origen, desti);
            double percentDespres = vehicle.obtenirBateria() - ((distTotal / vehicle.AUTONOMIA) * 100);

            if (vehicle.teBateria(distTotal, false) && percentDespres >= 20) {
                List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacioActual, origen);
                List<Lloc> camiFinsDesti = mapa.camiVoraç(origen, desti);

                ruta.afegirTramDesDeLlocs(camiFinsOrigen, mapa);
                ruta.afegirTramDesDeLlocs(camiFinsDesti, mapa);
                ruta.afegirPeticio(millor);

                vehicle.consumirBateria(distTotal);
                ubicacioActual = desti;
                pendents.remove(millor);
            } else {
                pendents.remove(millor);
            }
        }

        if (ruta.esBuida()) {
            Ruta rutaCarrega = mapa.rutaParquingMesProper(ubicacioActual, LocalTime.now(), this);
            if (rutaCarrega != null) {
                for (Cami cami : rutaCarrega.obtenirTrams()) {
                    ruta.afegirCami(cami);
                }
            }
        }

        return ruta;
    }

    @Override
    public Ruta planificarRuta(Peticio peticio, Mapa mapa) {
        Set<Peticio> peticions = new HashSet<>();
        peticions.add(peticio);
        return planificarRuta(mapa, peticions);
    }
    
    

    @Override
    public boolean potServirPeticio(int nombrePassatgers) {
        return !ocupat && nombrePassatgers <= vehicle.getMaxPassatgers();
    }

    @Override
    public boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici, LocalTime horaActual) {
        return vehicle.teBateria(distancia, false);
    }

    @Override
    public void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador) {
        for (Cami cami : ruta.obtenirTrams()) {
            Lloc desti = cami.obtenirDesti();
            double distancia = cami.obtenirDistancia();

            if (vehicle.teBateria(distancia, false)) {
                vehicle.moure(desti, distancia);
            } else {
                vehicle.carregarBateria(false);
                vehicle.moure(desti, distancia);
            }

            if (desti.esRecollida()) {
                int nPassatgers = ruta.obtenirNumPassatgers(desti);
                vehicle.afegirPassatgers(nPassatgers);
            } else if (desti.esDeixada()) {
                vehicle.alliberarPassatgers();
            }
        }
    }

    private Peticio seleccionarMillorPeticio(Lloc ubicacioActual, List<Peticio> peticions, Mapa mapa) {
        Peticio millor = null;
        double millorTemps = Double.POSITIVE_INFINITY;

        for (Peticio p : peticions) {
            Lloc origen = p.obtenirOrigen();
            Lloc desti = p.obtenirDesti();

            double distTotal = mapa.calcularDistancia(ubicacioActual, origen) +
                               mapa.calcularDistancia(origen, desti);
            double percentDespres = vehicle.obtenirBateria() - ((distTotal / vehicle.AUTONOMIA) * 100);

            if (vehicle.teBateria(distTotal, false) && percentDespres >= 20) {
                double temps = mapa.calcularTemps(ubicacioActual, origen) +
                               mapa.calcularTemps(origen, desti);
                if (temps < millorTemps) {
                    millor = p;
                    millorTemps = temps;
                }
            }
        }

        return millor;
    }
}