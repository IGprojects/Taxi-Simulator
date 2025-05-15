package core;

/**
 * @class PuntCarrega
 * @brief Classe que representa un punt de càrrega d'un vehicle elèctric.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.13
 */
public class PuntCarrega {
    private TipusPuntCarrega tipusCarga; /// < Tipus de punt de càrrega (ràpid o lent).
    private boolean ocupat; /// < Indica si el punt de càrrega està ocupat o no.

    public PuntCarrega(TipusPuntCarrega tipusCarga) {
        this.tipusCarga = tipusCarga;
        this.ocupat = false;
    }

    /**
     * @pre Cert.
     * @post Retorna el tipus de punt de càrrega.
     * 
     * @return TipusPuntCarrega
     */
    public TipusPuntCarrega getTipusCarga() {
        return this.tipusCarga;
    }

    /**
     * @pre Cert.
     * @post Assigna un nou tipus de punt de càrrega.
     * 
     */
    public void setTipusCarga(TipusPuntCarrega tipusCarga) {
        this.tipusCarga = tipusCarga;
    }

    /**
     * @pre Cert.
     * @post Retorna si el punt de càrrega està ocupat o no.
     * 
     * @return boolean
     */
    public boolean isOcupat() {
        return this.ocupat;
    }

    /**
     * @pre Cert.
     * @post Assigna si el punt de càrrega està ocupat o no.
     * 
     */
    public void setOcupat(boolean ocupat) {
        this.ocupat = ocupat;
    }

}
