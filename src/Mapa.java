/**
 * @class Mapa
 * @brief Mapa dels diferents llocs i connexions
 * @details Definirà el mapa i les connexions entre llocs
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Mapa {
    /**
     * @pre Cert
     *
     * @post Omple la llista de llocs i el mapa de connexions
     */
    public void carregarMapa(String fitxer) {
    }

    /**
     * @pre id>0
     *
     * @post Afegeix un nou lloc a la llista de llocs
     */
    public void afegirLloc(int id, String nom, boolean esCarrega, int capacitat, boolean carregaRapida) {
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Afegeix una nova connexio a la llista de connexions
     */
    public void afegirConnexio(Lloc origen, Lloc desti, double distancia, int temps) {
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna true si hi ha camí entre origen i desti, altrament false
     */
    public boolean hihaCami(int origen, Lloc desti) {
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna la distància en km que hi ha entre dos llocs
     */
    public double calcularDistancia(int origen, Lloc desti) {
    }

    /**
     * @pre Origen i desti existents
     *
     * @post Retorna el temps en min que hi ha entre dos llocs
     */
    public double calcularTemps(int origen, Lloc desti) {
    }
}
