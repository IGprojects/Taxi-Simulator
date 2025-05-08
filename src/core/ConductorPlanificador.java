package core;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalTime;
import java.util.ArrayList;


/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 */
public class ConductorPlanificador extends Conductor {
    public ConductorPlanificador(int id, String nom, Vehicle vehicle) {
        super(id, nom, vehicle);
    }
    public void decidirMoviment(Mapa mapa, List<Peticio> peticions) {
        //if (peticions.isEmpty()) {
            // No hi ha peticions pendents, anar al carregador privat
            //Lloc carregador = mapa.getCarregadorPrivatPredeterminat();
            //double dist = mapa.getDistancia(vehicle.getUbicacioActual().getId(), carregador.getId());
            //if (!vehicle.potFerViatge(dist)) {
              //  System.out.println("ATENCIÓ: El vehicle no pot arribar ni al punt de càrrega.");
                //return;
            //}

            //vehicle.moure(carregador, dist);
            //vehicle.consumirBateria(dist);
            //vehicle.carregarBateria(100.0);
            //return;
        //}
        Lloc ubicacioActual = vehicle.getUbicacioActual();
        List<Peticio> pendents = new ArrayList<>(peticions);
        Ruta ruta = new Ruta();
        boolean haTrobat = false;

        while (!pendents.isEmpty()) {
            Peticio millor = seleccionarMillorPeticio(ubicacioActual, pendents, mapa, vehicle);
            if (millor == null) break;

            Lloc origen = millor.obtenirOrigen();
            Lloc desti = millor.obtenirDesti();

            double distanciaTotal = mapa.getDistancia(ubicacioActual.getId(), origen) +
                                    mapa.getDistancia(origen.getId(), desti);

            double percentatgeDespres = vehicle.obtenirBateria() - ((distanciaTotal / vehicle.getAutonomia()) * 100);

            if (vehicle.potFerViatge(distanciaTotal) && percentatgeDespres >= 20) {
                // Executar directament
                vehicle.moure(origen, mapa.getDistancia(ubicacioActual.getId(), origen));
                vehicle.consumirBateria(mapa.getDistancia(ubicacioActual.getId(), origen));

                vehicle.moure(desti, mapa.getDistancia(origen.getId(), desti));
                vehicle.consumirBateria(mapa.getDistancia(origen.getId(), desti));

                // Simulem càrrega i descàrrega
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
            // No hi ha peticions viables → anar al carregador privat
            Lloc carregador = mapa.getCarregadorPrivatPredeterminat();
            double dist = mapa.getDistancia(ubicacioActual.getId(), carregador.getId());
            if (!vehicle.potFerViatge(dist)) {
                System.out.println("ATENCIÓ: El vehicle no pot arribar ni al punt de càrrega.");
                return;
            }

            vehicle.moure(carregador, dist);
            vehicle.consumirBateria(dist);
            vehicle.carregarBateria(100.0);
        }
    }

    public Ruta planificarRuta(Mapa mapa, Set<Peticio> peticions) {
        Ruta ruta = new Ruta();
        Lloc ubicacioActual = mapa.getCarregadorPrivatPredeterminat();
        Set<Peticio> pendents = new HashSet<>(peticions);

        while (!pendents.isEmpty()) {
            Peticio millor = seleccionarMillorPeticio(ubicacioActual, pendents, mapa, vehicle);
            if (millor != null) {
                Lloc origen = millor.obtenirOrigen();
                Lloc desti = millor.obtenirDesti();

                double distTotal = mapa.getDistancia(ubicacioActual.getId(), origen) +
                                   mapa.getDistancia(origen.getId(), desti);
                double percentDespres = vehicle.getBateria() - ((distTotal / vehicle.getAutonomia()) * 100);

                if (vehicle.potFerViatge(distTotal) && percentDespres >= 20) {
                    List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacioActual, origen);
                    List<Lloc> camiFinsDesti = mapa.camiVoraç(origen, desti);

                    ruta.afegirTram(camiFinsOrigen);
                    ruta.afegirTram(camiFinsDesti);
                    ruta.afegirPeticio(millor);

                    vehicle.consumirBateria(distTotal);
                    ubicacioActual = desti;
                    pendents.remove(millor);
                } else {
                    pendents.remove(millor); // petició no viable
                }
            } else {
                break;
            }
        }

        // Si no s'ha pogut planificar cap ruta viable, anar al carregador
        if (ruta.esBuida()) {
            List<Lloc> camiCarrega = mapa.camiVoraç(ubicacioActual, mapa.getCarregadorPrivatPredeterminat());
            ruta.afegirTram(camiCarrega);
        }

        return ruta;
    }

    public void executarRuta(Ruta r, Vehicle v) {
        for (Tram tram : r.obtenirTrams()) {
            Lloc desti = tram.obtenirDesti();
            double distancia = tram.obtenirDistancia();

            if (v.potFerViatge(distancia)) {
                v.moure(desti, distancia);
            } else {
                v.carregarBateria(100.0); // carrega completa al punt privat
                v.moure(desti, distancia);
            }

            if (desti.esRecollida()) {
                int nPassatgers = r.obtenirNumPassatgers(desti);
                v.afegirPassatgers(nPassatgers);
            } else if (desti.esDeixada()) {
                v.alliberarPassatgers();
            }
        }
    }

    private Peticio seleccionarMillorPeticio(Lloc ubicacioActual, List<Peticio> peticions, Mapa mapa, Vehicle vehicle) {
        Peticio millor = null;
        double millorTemps = Double.POSITIVE_INFINITY;

        for (Peticio p : peticions) {
            Lloc origen = p.obtenirOrigen();
            Lloc desti = p.obtenirDesti();

            double distTotal = mapa.getDistancia(ubicacioActual.getId(), origen) +
                               mapa.getDistancia(origen.getId(), desti);

            double percentDespres = vehicle.getBateria() - ((distTotal / vehicle.getAutonomia()) * 100);

            if (vehicle.potFerViatge(distTotal) && percentDespres >= 20) {
                double temps = mapa.getTemps(ubicacioActual.getId(), origen) +
                               mapa.getTemps(origen.getId(), desti);
                if (temps < millorTemps) {
                    millor = p;
                    millorTemps = temps;
                }
            }
        }

        return millor;
    }
    @Override
    public void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executarRuta'");
    }
    @Override
    public Ruta planificarRuta(Peticio peticio, Mapa mapa) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'planificarRuta'");
    }
    @Override
    public Ruta planificarCarrega(Mapa mapa, LocalTime horaInici) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'planificarCarrega'");
    }
    @Override
    public boolean teBateria(double distancia) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'teBateria'");
    }
}

