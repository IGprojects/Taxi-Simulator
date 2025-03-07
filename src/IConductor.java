import java.util.List;

/**
 * @interface IConductor
 * @brief Defineix el comportament bàsic d'un conductor dins la simulació.
 * @details Els conductors gestionen vehicles i prenen decisions en funció del
 *          mapa i les peticions disponibles.
 * 
 * @author Grup b9
 * @version 2025.03.04
 */
public interface IConductor {

    /**
     * @pre Cert.
     * @post Retorna el vehicle que condueix aquest conductor.
     *
     */
    Vehicle obtenirVehicle();

    /**
     * @pre v != null
     * @post S'assigna un vehicle al conductor.
     * 
     */
    void assignarVehicle(Vehicle v);

    /**
     * @pre Cert.
     * @post Retorna true si el conductor pot acceptar noves peticions, false en cas
     *       contrari.
     */
    boolean estaDisponible();

    /**
     * @pre p != null && estaDisponible()
     * @post La petició es guarda com la següent a realitzar pel conductor.
     * 
     * @param p Petició a assignar al conductor.
     */
    void assignarPeticio(Peticio p);

    /**
     * @pre Cert.
     * @post El conductor realitza el trajecte assignat i la petició es marca com
     *       completada.
     * 
     * @param r Ruta a executar
     * 
     */
    void executarRuta(Ruta r);

    /**
     * @brief Decideix el moviment del conductor segons l'estat actual de la
     *        simulació.
     * 
     * @pre mapa != null && peticions != null
     * @post El conductor ha decidit la seva acció següent.
     * 
     * @param mapa      Mapa de la simulació.
     * @param peticions Llista de peticions pendents.
     */
    void decidirMoviment(Mapa mapa, List<Peticio> peticions);

    /**
     * @pre Cert.
     * @post El vehicle incorpora el nombre de passatgers especificat a la petició.
     * 
     * @param numPass Nombre de passatgers a recollir.
     */
    void recollirPassatgers(int numPass);

}
