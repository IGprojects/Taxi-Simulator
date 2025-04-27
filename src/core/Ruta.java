package core;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @class Ruta
 * @brief Representa la ruta que segueix un vehicle per completar una petició.
 * @details Conté una seqüència de llocs per on passa la ruta, així com la
 * distància i el temps total del recorregut.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Ruta {
    private List<Lloc> llocs; ///< Llista de llocs que formen la ruta.
    private double distanciaTotal; ///< Distància total de la ruta.
    private double tempsTotal; ///< Temps total de la ruta.
    private LocalTime horaInici; ///< Hora d'inici de la ruta.
    private Conductor conductor; ///< Conductor que realitza la ruta.

    public Ruta(List<Lloc> llocs, LocalTime horaInici, double distanciaTotal, double tempsTotal, Conductor conductor) {
        this.llocs = llocs;
        this.distanciaTotal = distanciaTotal;
        this.tempsTotal = tempsTotal;
        this.horaInici = horaInici;
        this.conductor = conductor;
    }

    /**
     * @pre llocs != null
     * @post retorna la llista del llocs per on te que passar per fer la ruta.
     * 
     * @return llista de llocs que es passa per realitzar la ruta
     */
    public List<Lloc> getLlocs() {
        return this.llocs;
    }

    /**
     * @pre cami != null
     * @post Afegeix el destí del camí a la ruta i actualitza la distància i el
     * temps total.
     *
     * @param cami Camí a afegir a la ruta.
     */
    public void afegirCami(Cami cami) {
        llocs.add(cami.obtenirOrigen());
        llocs.add(cami.obtenirDesti());
        distanciaTotal += cami.obtenirDistancia();
        tempsTotal += cami.obtenirTemps();
    }

    /**
     * @pre Cert.
     * @post Retorna la distància acumulada de la ruta.
     *
     * @return Distància total en quilòmetres.
     */
    public double obtenirDistanciaTotal() {
        return distanciaTotal;
    }

    /**
     * @pre Cert.
     * @post Retorna el temps acumulat de la ruta.
     *
     * @return Temps total en minuts.
     */
    public double obtenirTempsTotal() {
        return tempsTotal;
    }

    /**
     * @pre Cert.
     * @post Retorna el conductor que realitza la ruta.
     *
     * @return Conductor que realitza la ruta.
     */
    public Conductor obtenirConductor() {
        return conductor;
    }

     /**
     * @pre Cert.
     * @post Retorna l'hora inicial.
     *
     * @return Hora inicial.
     */
    public LocalTime obtenirHoraInici() {
        return horaInici;
    }

    /**
     * @pre Cert.
     * @post Retorna true si la ruta conté llocs, false si està buida.
     *
     */
    public boolean esBuida() {
        return llocs.isEmpty();
    }


    public afegirPeticioPlanificada(Peticio peticio){
        

    }
}
