package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @param vehiclesTotals Llista completa de vehicles.
     * @return Llista de vehicles considerats redundants.
     */
    public List<Vehicle> obtenirVehiclesRedundants(File jsonFile, List<Vehicle> vehiclesTotals) {
        List<Vehicle> redundants = new ArrayList<>();
        List<Vehicle> copiaOriginal = new ArrayList<>(vehiclesTotals); // Còpia de seguretat

        // 1. Obtenir el nombre base de peticions no servides amb tots els vehicles
        Simulador simuladorCompleto = new Simulador(jsonFile, new ArrayList<>(vehiclesTotals));
        simuladorCompleto.iniciar(jsonFile,null);
        int peticionsNoServidesCompletes = simuladorCompleto.obtenirPeticionsNoServides();

        // 2. Avaluar cada vehicle individualment
        for (Vehicle vehicle : copiaOriginal) {
            List<Vehicle> vehiclesProva = new ArrayList<>(copiaOriginal);
            vehiclesProva.remove(vehicle);

            Simulador simuladorProva = new Simulador(jsonFile, vehiclesProva);
            simuladorProva.iniciar(jsonFile,null);
            int peticionsNoServidesProva = simuladorProva.obtenirPeticionsNoServides();

            // Si eliminar el vehicle no empitjora el servei, és redundant
            if (peticionsNoServidesProva <= peticionsNoServidesCompletes) {
                redundants.add(vehicle);
            }
        }

        return redundants;
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
    public Map<Integer, Integer> obtenirPuntsCarregaRedundants(File JsonFile) {
        Map<Integer, Integer> vegadesUsat;

        Simulador simuladorComplet = new Simulador(JsonFile, null);
        vegadesUsat = simuladorComplet.iniciarOptimitzacioPuntsCarrega(JsonFile);

        return vegadesUsat;
    }

}
