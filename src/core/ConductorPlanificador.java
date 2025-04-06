package core;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 */
public class ConductorPlanificador extends Conductor {


    /**
     * @pre vehicle != null && vehicle.teAutonomiaSuficient()
     * @post Crea un conductor planificador amb un vehicle assignat.            
     *      
     * @param vehicle Vehicle assignat al conductor.
     *   */
        public void decidirMoviment(Mapa mapa, Set<Peticio> peticions) {
            Ruta ruta = planificarRuta(mapa, peticions);
            executarRuta(ruta, vehicle);
        }

        /**
         * @pre mapa != null && peticions != null
         * @post Retorna una ruta planificada per al vehicle.
         *
         * @param mapa      El mapa de la simulació.
         * @param peticions Llista de peticions disponibles.
         * @return Ruta planificada.
         */
        public Ruta planificarRuta(Mapa mapa, Set<Peticio> peticions) {
            Ruta ruta = new Ruta();
            Lloc ubicacioActual = mapa.getCarregadorPrivatPredeterminat();
            Set<Peticio> pendents = new HashSet<>(peticions);

            while (!pendents.isEmpty()) {
                Peticio millor = seleccionarMillorPeticio(ubicacioActual, pendents, mapa);
                if (millor != null){

                    Lloc origen = millor.obtenirOrigen();
                    Lloc desti = millor.obtenirDesti();

                    List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacioActual, origen);
                    List<Lloc> camiFinsDesti = mapa.camiVoraç(origen, desti);

                    ruta.afegirCami(cacamiFinsOrigenmi);
                    ruta.afegirCami(camiFinsDesti);
                    ruta.afegirPeticioPlanificada(millor);

                    ubicacioActual = desti;
                    pendents.remove(millor);
                }
            }

            return ruta;
        }

        /**
         * @pre r != null && v != null
         * @post Executa la ruta planificada pel vehicle.
         *
         * @param r Ruta a executar.
         * @param v Vehicle que executarà la ruta.
         */

        @Override
        public void executarRuta(Ruta r, Vehicle v) {
            for (Tram tram : r.obtenirTrams()) {
                Lloc desti = tram.obtenirDesti();
                if (v.teAutonomiaSuficient(v.obtenirUbicacioActual(), desti)) {
                    v.moureFins(desti);
                } else {
                    v.carregarBateriaCompleta();
                }

                if (desti.esPuntDeRecollida()) {
                    v.recollirPassatgers(desti);
                } else if (desti.esPuntDeDeixada()) {
                    v.deixarPassatgers(desti);
                }
            }
        }
        
        /**
         * @pre peticions != null
         * @post Retorna la petició que millor s'ajusta al pla de càrrega.
         *
         * @param ubicacioActual La ubicació actual del vehicle.
         * @param peticions      Llista de peticions disponibles.
         * @param mapa           El mapa de la simulació.
         * @return Petició òptima seleccionada.
         */

        public Peticio seleccionarMillorPeticio(Lloc ubicacioActual, Set<Peticio> peticions, Mapa mapa) {
            Peticio millor = null;
            double millorTemps = Double.POSITIVE_INFINITY;

            for (Peticio p : peticions) {
                List<Lloc> cami = mapa.camiVoraç(ubicacioActual, p.obtenirOrigen());
                double temps = 0;

                for (int i = 0; i < cami.size() - 1; i++) {
                    temps += mapa.calcularTemps(cami.get(i), cami.get(i + 1));
                }

                if (temps < millorTemps) {
                    millor = p;
                    millorTemps = temps;
                }
            }

            return millor;
        }
    }
