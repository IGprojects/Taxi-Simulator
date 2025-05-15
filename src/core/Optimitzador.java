package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Optimitzador
 * @brief Classe que fa les operacions per optimitzar la simulació.
 * @details Processa els vehicles i punts de carrega que no siguin necessaris
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Optimitzador {

    public Optimitzador() {
    }

    /**
     * @brief Determina quins vehicles es poden suprimir sense afectar les
     * peticions.
     *
     * @pre El simulador està inicialitzat i conté una llista de vehicles.
     * @post Retorna una llista amb els vehicles que es poden eliminar sense
     * afectar el servei de peticions.
     *
     * @return Llista de vehicles redundants.
     */
    public List<Vehicle> obtenirVehiclesRedundants(File jsonFile,List<Vehicle>vehiclesTotals) {
        List<Vehicle> redundants = new ArrayList<>();

        for (Vehicle v : vehiclesTotals) {
            //comprobar si la simulacio es pot fer sense aquest vehicle
            vehiclesTotals.remove(v);
            Simulador simulador_Actual = new Simulador(jsonFile,vehiclesTotals);
            simulador_Actual.iniciar(jsonFile);
            //FALTA COMPROBAR SI LA CONDICIO ES CORRECTA AL FINALITZAR LA SIMULACIO
            
            if (!simulador_Actual.peticionsServides()) {
                //es torna a afegir el vehicle a la llista ja que sense ell no es pot completar la simulacio
                vehiclesTotals.add(v);                
            } else {
                redundants.add(v);
            }
        }
        return redundants;
    }
}
/**
 * @brief Determina quins punts de càrrega es poden eliminar sense afectar el
 * servei.
 *
 * @pre El simulador està inicialitzat i conté un mapa amb punts de càrrega.
 * @post Retorna una llista amb els punts de càrrega que es poden eliminar sense
 * afectar el servei de peticions.
 *
 * @return Llista de punts de càrrega redundants.
 */
/*   public List<PuntCarga> obtenirPuntsCarregaRedundants() {
        List<PuntCarga> redundants = new ArrayList<>();

       /* List<PuntCarrega> puntsCarrega = simulador.getMapa().getPuntsCarrega();


        for (PuntCarrega p : puntsCarrega) {
            simulador.getMapa().eliminarPuntCarrega(p);
            if (!simulador.potServirTotesLesPeticions()) {
                simulador.getMapa().afegirPuntCarrega(p);
            } else {
                redundants.add(p);
            }
        }
        return redundants;
    }*/
