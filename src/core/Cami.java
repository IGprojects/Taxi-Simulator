package core;
/**
 * @class Cami
 * @brief Representa un camí entre dos llocs del mapa.
 * @details Cada camí és unidireccional i conté informació sobre la distància i
 *          el temps estimat de trajecte.
 * 
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Cami {

    Lloc origen; ///< Punt d'origen del camí.
    Lloc desti; ///< Punt de destí del camí.
    double distancia; ///< Distància en quilòmetres.
    double temps; ///< Temps estimat en minuts.

    /** 
     * @pre Cert.
     * @post Retorna el punt d'origen del camí.
     *
     */
    public Lloc obtenirOrigen() {
        return origen;
    }

    /**
     * @pre Cert.
     * @post Retorna el punt de destí del camí.
     *
     */
    public Lloc obtenirDesti() {
        return desti;
    }

    /**
     * @pre Cert.
     * @post Retorna la distància del camí.
     *
     */
    public double obtenirDistancia() {
        return distancia;
    }

    /**
     * @pre Cert.
     * @post Retorna el temps estimat per recórrer el camí.
     *
     */
    public double obtenirTemps() {
        return temps;
    }

}
