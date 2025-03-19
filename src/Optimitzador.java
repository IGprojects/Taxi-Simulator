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

private Simulador simulador;

    public Optimitzador(Simulador simulador) {
        this.simulador = simulador;
    }

    /**
     * @brief Determina quins vehicles es poden suprimir sense afectar les peticions.
     * 
     * @pre El simulador està inicialitzat i conté una llista de vehicles.
     * @post Retorna una llista amb els vehicles que es poden eliminar sense afectar el servei de peticions.
     * 
     * @return Llista de vehicles redundants.
     */
    public List<Vehicle> obtenirVehiclesRedundants() {
        List<Vehicle> redundants = new ArrayList<>();

        /*List<Vehicle> vehicles = simulador.getVehicles();

        for (Vehicle v : vehicles) {
            simulador.eliminarVehicle(v);
            if (!simulador.potServirTotesLesPeticions()) {
                simulador.afegirVehicle(v);
            } else {
                redundants.add(v);
            }
        }*/
        return redundants;
    }

    /**
     * @brief Determina quins punts de càrrega es poden eliminar sense afectar el servei.
     * 
     * @pre El simulador està inicialitzat i conté un mapa amb punts de càrrega.
     * @post Retorna una llista amb els punts de càrrega que es poden eliminar sense afectar el servei de peticions.
     * 
     * @return Llista de punts de càrrega redundants.
     */
    public List<PuntCarga> obtenirPuntsCarregaRedundants() {
        List<PuntCarga> redundants = new ArrayList<>();

       /* List<PuntCarrega> puntsCarrega = simulador.getMapa().getPuntsCarrega();


        for (PuntCarrega p : puntsCarrega) {
            simulador.getMapa().eliminarPuntCarrega(p);
            if (!simulador.potServirTotesLesPeticions()) {
                simulador.getMapa().afegirPuntCarrega(p);
            } else {
                redundants.add(p);
            }
        }*/
        return redundants;
    }
}



