package core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import events.Event;

/**
 * @class Optimitzador
 * @brief Classe encarregada d’optimitzar la simulació.
 * @details Processa els vehicles i punts de càrrega per identificar-ne aquells
 * que no són necessaris
 *
 * @author Ignasi Ferrés Iglesias
 * @version 2025.05.13
 */
public class Optimitzador {

    /**
     * @brief Constructor per defecte.
     */
    public Optimitzador() {
    }

    /**
     * @brief Determina quins vehicles es poden eliminar sense augmentar les
     * peticions no servides.
     *
     * @pre El simulador està inicialitzat i conté una llista de vehicles.
     * @post Retorna una llista amb els vehicles que poden ser eliminats sense
     * empitjorar el servei.
     *
     * @param jsonFile Fitxer JSON amb la configuració de la simulació.
     * @return Llista de vehicles considerats redundants.
     */
    public Map<Integer, Integer> obtenirConductorsRedundants(File JsonFile) throws IOException {
        Map<Integer, Integer> vegadesUsat = new HashMap<>();

        Map<Integer, Lloc> llocsID = LectorJSON.convertirLlistaAMap_Llocs(LectorJSON.carregarLlocs(JsonFile.getAbsolutePath()));
        Map<Integer, Vehicle> vehiclesPerId = LectorJSON.convertirLlistaAMap_Vehicles(LectorJSON.carregarVehicles(JsonFile.getAbsolutePath(), llocsID));
        List<Conductor> conductorsTotals = LectorJSON.carregarConductors(JsonFile.getAbsolutePath(), vehiclesPerId, llocsID);

        // Obtenim els events
        List<Event> LlistaEventsExec = LectorJSON.carregarEvents2(JsonFile.getAbsolutePath(), vehiclesPerId, LectorJSON.convertirLlistaAMap_Conductors(conductorsTotals), llocsID);

        // Obtenim conductors que no han deixat passatgers
        vegadesUsat = LectorJSON.obtenirUsDeixarPassatgersPerConductor(LlistaEventsExec, conductorsTotals);

        // Imprimim per verificar
        System.out.println("Conductors redundants (sense deixar passatgers): " + vegadesUsat.keySet());

        return vegadesUsat;
    }

    /**
     * @brief Determina quins punts de càrrega són redundants basant-se en el
     * seu ús.
     *
     * @pre El mapa de punts de càrrega i el registre d'ús han d'estar
     * inicialitzats.
     * @post Retorna un mapa amb els identificadors dels punts de càrrega i el
     * seu nombre d'usos.
     *
     * @param JsonFile Fitxer JSON amb la configuració de la simulació.
     * @return Map amb identificadors de punts de càrrega i el nombre de vegades
     * que s'han utilitzat.
     */
    public Map<Integer, Integer> obtenirPuntsCarregaRedundants(File JsonFile) throws IOException {
        Map<Integer, Integer> vegadesUsat = new HashMap<>();
        Map<Integer, Lloc> llocsID = LectorJSON.convertirLlistaAMap_Llocs(LectorJSON.carregarLlocs(JsonFile.getAbsolutePath()));
        Map<Integer, Vehicle> vehiclesPerId = LectorJSON.convertirLlistaAMap_Vehicles(LectorJSON.carregarVehicles(JsonFile.getAbsolutePath(), llocsID));
        List<Conductor> conductorsTotals = LectorJSON.carregarConductors(JsonFile.getAbsolutePath(), vehiclesPerId, llocsID);
        List< Lloc> llocs = LectorJSON.carregarLlocs(JsonFile.getAbsolutePath());

        // Obtenim els events
        List<Event> LlistaEventsExec = LectorJSON.carregarEvents2(JsonFile.getAbsolutePath(), vehiclesPerId, LectorJSON.convertirLlistaAMap_Conductors(conductorsTotals), llocsID);

        // Obtenim conductors que no han deixat passatgers
        vegadesUsat = LectorJSON.obtenirUsosPuntsCarrega(LlistaEventsExec, llocs);

        // Imprimim per verificar
        System.out.println("Punts de carrega redundants : " + vegadesUsat.keySet());

        return vegadesUsat;
    }

}
