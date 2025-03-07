import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @class Simulador
 * @brief Classe que gestiona l'execució de la simulació.
 * @details Controla els vehicles, conductors i peticions, processant-les en
 *          funció del temps.
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Simulador {

    /**
     * @pre v != null
     * @post El vehicle s'afegeix a la llista de vehicles disponibles per la
     *       simulació.
     *
     * @param v Vehicle a afegir.
     */
    public void afegirVehicle(Vehicle v);

    /**
     * @pre c != null
     * @post El conductor s'afegeix a la llista de conductors disponibles per la
     *       simulació.
     *
     * @param c Conductor a afegir.
     */
    public void afegirConductor(Conductor c);

    /**
     * @pre p != null
     * @post La petició es registra i queda pendent d’assignació a un vehicle.
     *
     * @param p Petició a afegir.
     */
    public void afegirPeticio(Peticio p);

    /**
     * @pre cert
     * @post Es genera una petició de trasllat amb dades aleatòries i s'afegeix a la
     *       llista de peticions.
     */
    public void afegirPeticioAleatoria();

    /**
     * @pre cert
     * @post Inicia l'execució de la simulació.
     *       
     */
    public void iniciar();

    /**
     * @pre !peticions.isEmpty()
     * @post S’assigna la petició a un vehicle si és possible i es realitza el
     *       viatge.
     */
    private void seguentPeticio();

    /**
     * @pre cert
     * @post Tanca la simulació i mostra un resum dels resultats.
     */
    private void finalitzar();

    /**
     * @brief Calcula estadístiques sobre la simulació.
     *
     * @pre Cert
     * @post Retorna els resultats estadístics, incloent peticions completades i
     *       temps mitjans.
     *
     * @return Objecte de tipus CalculEstadistic amb els resultats de la simulació.
     */
    public CalculsEstadistics calculsEstadistics();
}
