import java.util.List;

/**
 * @class Conductor
 * @brief Defineix la classe pare dels tipus de conductors
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public abstract class Conductor{

    private Peticio peticio;


    

    /**
     * @pre Cert.
     * @post Retorna si el conductor està disponible.
     */
    public void executarRuta(Ruta r){

    }

    /**
     * @pre Cert.
     * @post Decideix el moviment del conductor basant-se en el mapa i les peticions.
     */
    public abstract void decidirMoviment(Mapa mapa, List<Peticio> peticions);


    
    
}
