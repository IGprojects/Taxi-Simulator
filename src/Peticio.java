import java.util.Date;

/**
 * @class Peticio
 * @brief Representa una petició de transport feta per un client.
 * @details Conté la informació necessària per determinar si un vehicle pot
 *          satisfer la petició.
 * 
 * @author Grup b9
 * @version 2025.03.04
 */
public class Peticio {
    /**
     * @pre Cert.
     * @post Retorna el lloc on el client vol ser recollit.
     *
     */
    public Lloc obtenirOrigen();

    /**
     * @pre Cert.
     * @post Retorna el lloc on el client vol arribar.
     *
     */
    public Lloc obtenirDesti();

    /**
     * @pre Cert.
     * @post Retorna l'hora més primerenca en què el client vol ser recollit.
     *
     */
    public Date obtenirHoraMinimaRecollida();

    /**
     * @pre Cert.
     * @post Retorna l'hora màxima en què el client vol arribar al seu destí.
     *
     */
    public Date obtenirHoraMaximaArribada();

    /**
     * @pre Cert.
     * @post Retorna el nombre de passatgers que han de viatjar.
     *
     */
    public int obtenirNumPassatgers();

    /**
     * @pre Cert.
     * @post Retorna true si el client vol un vehicle compartit, false en cas
     *       contrari.
     *
     */
    public boolean esVehicleCompartit() ;
}
