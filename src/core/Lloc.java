package core;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Lloc
 * @brief Característiques de cada lloc
 * @details Definirà els atributs de cada lloc
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Lloc {

    protected int capacitatMaxima; /// < Capacitat màxima de vehicles en el lloc
    protected int id; /// < Identificador del lloc
    private int vehiclesActuals; /// < Nombre de vehicles actuals en el lloc
    private boolean esPrivat; /// < Indica si el lloc és un carregador privat
    private boolean esRecollida;
    private boolean esDeixada;


    public Lloc(int id, int capacitatMaxima) {
        this.id = id;
        this.capacitatMaxima = capacitatMaxima;
        this.vehiclesActuals = 0;
    }

    /**
     * @pre Cert.
     * @post Retorna un enter representant la capacitat màxima de vehicles en el
     *       lloc.
     * 
     * @return Capacitat màxima de vehicles en el lloc.
     */
    public int obtenirCapacitatMaxima() {
        return capacitatMaxima;
    }

    /**
     * @pre Cert
     * @post Si hi ha espai, el nombre de passatgers augmenta.
     * 
     * @return True si s'ha afegit, altrament fals.
     */
    boolean entrarVehicle() {
        if (!estaPle()) {
            vehiclesActuals++;
            return true;
        }
        return false;
    }

    /**
     * @pre Cert
     * @post El nombre de passatgers disminueix en 1.
     * 
     */
    void sortirVehicle() {
        vehiclesActuals--;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de vehicles que actualment estan en el lloc.
     * 
     * @return Nombre de vehicles presents en el lloc.
     */
    int obtenirVehiclesActuals() {
        return vehiclesActuals;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el nombre de vehicles ha assolit el màxim permès, false
     *       en cas contrari.
     * 
     * @return true si el lloc està ple, false en cas contrari.
     */
    boolean estaPle() {
        return vehiclesActuals == capacitatMaxima;
    }

    /**
     * @pre Cert.
     * @post Retorna l'identificador del lloc.
     * 
     * @return Identificador del lloc.
     */
    public int obtenirId() {
        return id;
    }

        public boolean esCarregadorPrivat() {
        return this.esPrivat;
    }

        /**
     * @pre Cert.
     * @post Retorna true si el lloc és punt de recollida.
     */
    public boolean esRecollida() {
        return esRecollida;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el lloc és punt de deixada.
     */
    public boolean esDeixada() {
        return esDeixada;
    }

    /**
     * @pre Cert.
     * @post Marca aquest lloc com a punt de recollida.
     */
    public void marcarComRecollida() {
        this.esRecollida = true;
    }

    /**
     * @pre Cert.
     * @post Marca aquest lloc com a punt de deixada.
     */
    public void marcarComDeixada() {
        this.esDeixada = true;
    }


}
