package core;

/**
 * @class Conductor
 * @brief Defineix la classe pare dels tipus de conductors
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public abstract class Conductor {
    protected int id; /// < Identificador del conductor.
    protected String nom; /// < Nom del conductor.
    private Peticio peticio;//Peticio actual del contuctor
    protected Vehicle vehicle;//Vehicle que condueix el conductor

    public Conductor(int id, String nom, Vehicle vehicle) {
        this.id = id;
        this.nom = nom;
        this.vehicle = vehicle;
    }
    
    /**
     * @pre Cert.
     * @post Retorna si el conductor està disponible.
     */
    public abstract void executarRuta(Ruta r, Vehicle v);
    
    

}
