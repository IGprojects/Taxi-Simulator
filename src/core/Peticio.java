package core;
import java.util.Date;
import java.util.PriorityQueue;

import org.w3c.dom.events.Event;

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

    private Lloc origen; /// < Lloc on el client vol ser recollit.
    private Lloc desti; /// < Lloc on el client vol arribar.
    private Date horaMinimaRecollida; /// < Hora mínima en què el client vol ser recollit.
    private Date horaMaximaArribada; /// < Hora màxima en què el client vol arribar al destí.
    private int numPassatgers; /// < Nombre de passatgers que han de viatjar.
    private boolean vehicleCompartit; /// < Indica si el client vol un vehicle compartit.
    private EstatPeticio estat; /// < Estat actual de la petició.

    public EstatPeticio estatActual() {
        return estat;
    }

    /**
     * @pre Cert.
     * @post Retorna el lloc on el client vol ser recollit.
     *
     */
    public Lloc obtenirOrigen() {
        return origen;
    }

    /**
     * @pre Cert.
     * @post Retorna el lloc on el client vol arribar.
     *
     */
    public Lloc obtenirDesti() {
        return desti;
    }

    /**
     * @pre Cert.
     * @post Retorna l'hora més primerenca en què el client vol ser recollit.
     *
     */
    public Date obtenirHoraMinimaRecollida() {
        return horaMinimaRecollida;
    }

    /**
     * @pre Cert.
     * @post Retorna l'hora màxima en què el client vol arribar al seu destí.
     *
     */
    public Date obtenirHoraMaximaArribada() {
        return horaMaximaArribada;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de passatgers que han de viatjar.
     *
     */
    public int obtenirNumPassatgers() {
        return numPassatgers;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el client vol un vehicle compartit, false en cas
     *       contrari.
     *
     */
    public boolean esVehicleCompartit() {
        return vehicleCompartit;
    }

    /**
     * @pre Cert.
     * @post La petició passa a l'estat SERVIDA.
     *
     */
    public void peticioServida() {
        estat = EstatPeticio.SERVIDA;
    }

    /**
     * @pre Cert.
     * @post La petició passa a l'estat EN_PROCES.
     *
     */
    public void peticioEnProces() {
        estat = EstatPeticio.EN_PROCES;
    }
}
