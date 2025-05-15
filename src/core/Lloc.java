package core;

/**
 * @class Lloc
 * @brief Característiques de cada lloc
 * @details Definirà els atributs de cada lloc
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Lloc {

    protected int id; /// < Identificador del lloc

    public Lloc(int id) {
        this.id = id;
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

}
