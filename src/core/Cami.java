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

    Lloc origen; /// < Punt d'origen del camí.
    Lloc desti; /// < Punt de destí del camí.
    double distancia; /// < Distància en quilòmetres.
    double temps; /// < Temps estimat en minuts.

    /**
     * @pre Cert.
     * @post Crea un camí entre dos llocs amb la distància i el temps estimat
     *       especificats.
     *
     * @param origen    Punt d'origen del camí.
     * @param desti     Punt de destí del camí.
     * @param distancia Distància en quilòmetres.
     * @param temps     Temps estimat en minuts.
     */
    public Cami(Lloc origen, Lloc desti, double distancia, double temps) {
        this.origen = origen;
        this.desti = desti;
        this.distancia = distancia;
        this.temps = temps;
    }

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
     * @post Retorna true si el camí és igual a l'altre, false en cas contrari.
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Cami altre = (Cami) obj;
        return origen.equals(altre.origen) && desti.equals(altre.desti);
    }

    /**
     * @pre Cert.
     * @post Retorna un enter que representa el hash del camí.
     *
     */
    @Override
    public int hashCode() {
        return origen.hashCode() * 31 + desti.hashCode();
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
