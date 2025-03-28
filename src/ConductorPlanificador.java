import java.util.List;

/**
 * @class ConductorPlanificador
 * @brief Conductor que planifica rutes entre càrregues completes.
 */
public class ConductorPlanificador extends Conductor {

    @Override
    public void decidirMoviment(Mapa mapa, List<Peticio> peticions);

    @Override
    public Ruta planificarRuta(Mapa mapa, List<Peticio> peticio, Vehicle v) {
        /*
         * Algorisme de Backtracking per trobar la millor ruta d’un conductor
         * "planificador".
         * Entrada:
         * - peticions: llista de peticions pendents
         * - vehicle: vehicle assignat al conductor
         * Sortida:
         * - Millor seqüència de peticions possibles entre cada càrrega
         * 
         * planificarRuta(peticions, vehicle):
         * millorRuta = []
         * millorDistancia = 0
         * 
         * // Funció recursiva per buscar la millor combinació de peticions
         * backtracking(rutaActual, autonomiaRestant, distanciaTotal):
         * SI No Hi Ha Més Peticions Pendents LLAVORS
         * SI distanciaTotal > millorDistancia LLAVORS
         * millorRuta = Copia(rutaActual)
         * millorDistancia = distanciaTotal
         * FSI
         * FSI
         * 
         * Inicialitzar Conjunt De Candidats (peticionsDisponibles)
         * 
         * MENTRE Queden Candidats FER
         * peticio = Seguent Candidat
         * 
         * SI Acceptable(peticio, autonomiaRestant) LLAVORS
         * Anotar Candidat (rutaActual.afegir(peticio))
         * novaAutonomia = autonomiaRestant - DistanciaNecessària(peticio)
         * novaDistancia = distanciaTotal + DistanciaNecessària(peticio)
         * 
         * SI NO Solucio Completa LLAVORS
         * Backtracking(rutaActual, novaAutonomia, novaDistancia)
         * ALTRAMENT
         * SI novaDistancia > millorDistancia LLAVORS
         * millorRuta = Copia(rutaActual)
         * millorDistancia = novaDistancia
         * FSI
         * FSI
         * 
         * Desanotar Candidat (rutaActual.eliminar(peticio))
         * FSI
         * 
         * Seguent Candidat
         * FMENTRE
         * 
         * // Iniciar backtracking amb bateria al 100%
         * backtracking([], vehicle.getAutonomiaMaxima(), 0)
         * Retornar millorRuta
         */
    }

    @Override
    public void executarRuta(Ruta r, Vehicle v) {
        /*
         * Algorisme per executar la ruta planificada
         * Entrada:
         * - ruta: seqüència de llocs que el vehicle ha de seguir
         * - vehicle: vehicle que executa la ruta
         * Sortida:
         * - Informe de peticions ateses i estat final del vehicle
         * 
         * executarRuta(ruta, vehicle):
         * per cada lloc en ruta:
         * SI vehicle.teAutonomiaSuficient(vehicle.ubicacioActual, lloc) LLAVORS
         * vehicle.moureFins(lloc)
         * SI NO LLAVORS
         * vehicle.carregarBateriaCompleta()
         * FSI
         * 
         * SI lloc.ésPuntDeRecollida() LLAVORS
         * vehicle.recollirPassatgers(lloc)
         * FSI
         * 
         * SI lloc.ésPuntDeDeixada() LLAVORS
         * vehicle.deixarPassatgers(lloc)
         * FSI
         * 
         * Retornar "Ruta completada"
         */

    }

    /**
     * @pre peticions != null
     * @post Retorna la petició que millor s'ajusta al pla de càrrega.
     *
     * @param peticions Llista de peticions disponibles.
     * @param mapa      El mapa de la simulació.
     * @return Petició òptima seleccionada.
     */
    //
    private Peticio seleccionarMillorPeticio(List<Peticio> peticions, Mapa mapa);
}



 public Ruta planificarRuta(Mapa mapa, Set<Peticio> peticions) {
    Ruta ruta = new Ruta();
    Lloc ubicacioActual = mapa.getCarregadorPrivatPredeterminat();

    // Ja assumim que el Set està ordenat, així que només iterem
    for (Peticio peticio : peticions) {
        Lloc origen = peticio.obtenirOrigen();
        Lloc desti = peticio.obtenirDesti();

        List<Lloc> camiFinsOrigen = mapa.dijkstra(ubicacioActual, origen);
        List<Lloc> camiFinsDesti = mapa.dijkstra(origen, desti);

        ruta.afegirTram(camiFinsOrigen);
        ruta.afegirTram(camiFinsDesti);
        ruta.afegirPeticioPlanificada(peticio);

        ubicacioActual = desti; // Ens preparem per a la següent petició
    }

    return ruta;
}

public Ruta planificarRuta(Mapa mapa, Set<Peticio> peticions) {
    Ruta ruta = new Ruta();
    Lloc ubicacioActual = mapa.getCarregadorPrivatPredeterminat();
    Set<Peticio> pendents = new HashSet<>(peticions);

    while (!pendents.isEmpty()) {
        Peticio millor = seleccionarMillorPeticio(ubicacioActual, pendents, mapa);
        if (millor == null) break;

        Lloc origen = millor.obtenirOrigen();
        Lloc desti = millor.obtenirDesti();

        List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacioActual, origen);
        List<Lloc> camiFinsDesti = mapa.camiVoraç(origen, desti);

        ruta.afegirTram(camiFinsOrigen);
        ruta.afegirTram(camiFinsDesti);
        ruta.afegirPeticioPlanificada(millor);

        ubicacioActual = desti;
        pendents.remove(millor);
    }

    return ruta;
}


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
