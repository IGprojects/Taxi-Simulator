/**
 * @class Cami
 * @brief Representa un camí entre dos llocs del mapa.
 * @details Cada camí és unidireccional i conté informació sobre la distància i
 *          el temps estimat de trajecte.
 * 
 * @author Grup b9
 * @version 2025.03.04
 */
public class Cami {

    /**
     * @pre Cert.
     * @post Retorna el punt d'origen del camí.
     *
     */
    public Lloc obtenirOrigen();

    /**
     * @pre Cert.
     * @post Retorna el punt de destí del camí.
     *
     */
    public Lloc obtenirDesti();

    /**
     * @pre Cert.
     * @post Retorna la distància del camí.
     *
     */
    public double obtenirDistancia();

    /**
     * @pre Cert.
     * @post Retorna el temps estimat per recórrer el camí.
     *
     */
    public double obtenirTemps();

    /**
     * @brief Modifica la distància del camí.
     *
     * @pre novaDistancia > 0
     * @post La distància del camí s’ha actualitzat.
     *
     * @param novaDistancia Nova distància en quilòmetres.
     */
    public void modificarDistancia(double novaDistancia);

    /**
     * @brief Modifica el temps estimat de trajecte.
     *
     * @pre nouTemps > 0
     * @post El temps del camí s’ha actualitzat.
     *
     * @param nouTemps Nou temps estimat en minuts.
     */
    public void modificarTemps(double nouTemps);

}
