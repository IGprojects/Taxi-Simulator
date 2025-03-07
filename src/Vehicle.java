/**
 * @class Vehicle
 * @brief Defineix el vehicle i les seves característiques
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Vehicle {

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle té bateria (>0%), altrament false.
     * @return true si el vehicle té bateria disponible, false si està esgotada.
     */
    public boolean teBateria();

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle està ple, altrament false.
     */
    public boolean esPle();

    /**
     * @pre numPass >= 0
     * @post S'afegeixen els passatgers especificats al vehicle si hi ha espai
     *       suficient.
     *
     * @param numPass Nombre de passatgers a afegir.
     */
    public void afegirPassatgers(int numPass);

    /**
     * @brief Allibera tots els passatgers del vehicle.
     *
     * @pre Cert
     * @post El vehicle queda buit sense passatgers.
     */
    public void alliberarPassatgers();

    /**
     * @pre Cert.
     * @post Retorna el nombre de passatgers actuals del vehicle.
     */
    public int passatgersActuals();

    /**
     * @pre percentatge > 0 i percentatge <= 100.
     * @post La bateria s'incrementa fins al percentatge indicat sense superar el
     *       100%.
     *
     * @param percentatge Percentatge al qual es vol carregar la bateria.
     */
    public void carregarBateria(double percentatge);

    /**
     * @pre distancia >= 0.
     * @post La bateria es redueix en funció de la distància recorreguda.
     *
     * @param distancia Distància en quilòmetres que el vehicle ha recorregut.
     */
    public void consumirBaateria(double distancia);

    /**
     * @pre Cert.
     * @post Retorna el nivell de bateria en percentatge (0% - 100%).
     */
    public double obtenirBateria();

    /**
     * @pre Cert.
     * @post Retorna true si la bateria està per sota del 20%, false en cas
     *       contrari.
     *
     * @return true si la bateria és baixa, false si encara és suficient.
     */
    public boolean bateriaBaixa();

    /**
     * @pre distancia >= 0.
     * @post Retorna true si la bateria permet completar la distància, false si no
     *       n'hi ha prou.
     *
     * @param distancia Distància a recórrer en quilòmetres.
     */
    public boolean potFerViatge(double distancia);

    /**
     * @pre novaUbicacio != null i distancia >= 0.
     * @post El vehicle es desplaça a la nova ubicació i la seva bateria es redueix
     *       en funció de la distància recorreguda.
     *
     * @param novaUbicacio Lloc on es mourà el vehicle.
     * @param distancia    Distància recorreguda per arribar a la nova ubicació.
     */
    public void moure(Lloc novaUbicacio, double distancia);

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle està carregant, false en cas contrari.
     */
    public boolean esCarregant();

}
