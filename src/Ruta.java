/**
 * @class Ruta
 * @brief Representa la ruta que segueix un vehicle per completar una petició.
 * @details Conté una seqüència de llocs per on passa la ruta, així com la
 *          distància i el temps total del recorregut.
 * 
 * @author Grup b9
 * @version 2025.03.04
 */
public class Ruta {

    /**
     * @pre cami != null
     * @post Afegeix el destí del camí a la ruta i actualitza la distància i el
     *       temps total.
     * 
     * @param cami Camí a afegir a la ruta.
     */
    public void afegirCami(Cami cami);

    /**
     * @pre Cert.
     * @post Retorna la distància acumulada de la ruta.
     * 
     * @return Distància total en quilòmetres.
     */
    public double obtenirDistanciaTotal();

    /**
     * @pre Cert.
     * @post Retorna el temps acumulat de la ruta.
     * 
     * @return Temps total en minuts.
     */
    public double obtenirTempsTotal();

    /**
     * @pre Cert.
     * @post Retorna true si la ruta conté llocs, false si està buida.
     * 
     */
    public boolean esBuida();
}
