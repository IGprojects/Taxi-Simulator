import java.util.ArrayList;
import java.util.List;

/**
 * @class Lloc
 * @brief Característiques de cada lloc
 * @details Definirà els atributs de cada lloc
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Lloc {
    /**
     * @pre Cert.
     * @post Retorna un enter representant la capacitat màxima de vehicles en el
     *       lloc.
     * 
     * @return Capacitat màxima de vehicles en el lloc.
     */
    int obtenirCapacitatMaxima();

    /**
     * @pre Cert
     * @post Si hi ha espai, el nombre de passatgers augmenta.
     * 
     * @return True si s'ha afegit, altrament fals.
     */
    boolean entrarVehicle();

    /**
     * @pre Cert
     * @post El nombre de passatgers disminueix en 1.
     * 
     */
    void sortirVehicle();

    /**
     * @pre Cert.
     * @post Retorna el nombre de vehicles que actualment estan en el lloc.
     * 
     * @return Nombre de vehicles presents en el lloc.
     */
    int obtenirVehiclesActuals();

    /**
     * @pre Cert.
     * @post Retorna true si el nombre de vehicles ha assolit el màxim permès, false
     *       en cas contrari.
     * 
     * @return true si el lloc està ple, false en cas contrari.
     */
    boolean estaPle();

}
