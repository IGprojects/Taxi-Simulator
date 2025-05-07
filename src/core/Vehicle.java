package core;


/**
 * @class Vehicle
 * @brief Defineix el vehicle i les seves característiques
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Vehicle {

    final int MAXPASSATGERS; ///< Nombre màxim de passatgers que pot transportar el vehicle.
    final int AUTONOMIA; ///< Autonomia màxima del vehicle en quilòmetres.
    final double TEMPSCARGARAPIDA; ///< Temps en minuts per carregar la bateria amb carga ràpida.
    final double TEMPSCARGALENTA; ///< Temps en minuts per carregar la bateria amb carga lenta.

    private int id; ///< Identificador del vehicle.
    private Lloc ubicacio; ///< Lloc on es troba actualment el vehicle.
    private double bateria; ///< Nivell actual de bateria del vehicle (0% - 100%).
    private int numPassatgers; ///< Nombre de passatgers actuals del vehicle.
    private boolean carregant; ///< Indica si el vehicle està carregant.

    /**
     * Constructor de la classe Vehicle.
     *
     * @param ubicacio La ubicació inicial del vehicle.
     */
    public Vehicle(int id, Lloc ubicacio ,int maxpassatgers, int autonomia,double tempsCargaLenta,double tempsCargaRapida) {
        this.id = id;
        this.ubicacio = ubicacio;
        this.bateria = 100.0;           // La bateria comença plena
        this.numPassatgers = 0;         // El vehicle comença buit
        this.AUTONOMIA = autonomia;
        this.MAXPASSATGERS = maxpassatgers;
        this.TEMPSCARGALENTA=tempsCargaLenta;
        this.TEMPSCARGARAPIDA=tempsCargaRapida;
        this.carregant = false;         // El vehicle no està carregant inicialment
    }

    public int getId() {
        return id;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle té bateria (>0%), altrament false.
     * @return true si el vehicle té bateria disponible, false si està esgotada.
     */
    public boolean teBateria(double km) {
        return this.bateria > km;
    }


    /**
     * @pre Cert.
     * @post Retorna la ubicacio actual del vehicle
     * @return Lloc on es troba actualment el conductor
     */
    public Lloc getUbicacioActual(){
        return this.ubicacio;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle està ple, altrament false.
     */
    public boolean esPle() {
        return MAXPASSATGERS == numPassatgers;
    }

    /**
     * @pre nPassatgers < MAXPassatgers @post S 'afegeixen els passatgers
     * especificats al vehicle si hi ha espai suficient.
     *
     * @param nPassatgers Nombre de passatgers a afegir.
     */
    public void afegirPassatgers(int nPassatgers) {

        numPassatgers += nPassatgers;
    }

    /**
     * @brief Allibera tots els passatgers del vehicle.
     *
     * @pre Cert
     * @post El vehicle queda buit sense passatgers.
     */
    public void alliberarPassatgers() {
        this.numPassatgers = 0;
    }

    /**
     * @brief Allibera un numero concrey de passatgers del vehicle.
     *
     * @pre Cert
     * @post El vehicle queda buit sense passatgers.
     */
    public void alliberarPassatgersConcret(int n) {
        this.numPassatgers -= n;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de passatgers actuals del vehicle.
     */
    public int passatgersActuals() {
        return this.numPassatgers;
    }

    /**
     * @pre bateria >= 0.
     * @post Incrementa el nivell de bateria segons el percentatge indicat.
     *
     * @param percentatge Percentatge a carregar (ha d'estar en l'interval [0,
     * 100]).
     */
    public void carregarBateria(double percentatge) {
        if (percentatge > 0) {
            bateria += percentatge;  // Carregar bateria
            if (bateria > 100) {
                bateria = 100;  // No permetre superar el 100%
            }
        }
    }

    /**
     * @pre bateria >0.
     * @post La bateria es redueix en funció de la distància recorreguda.
     *
     * @param distancia Distància en quilòmetres que el vehicle ha recorregut.
     */
    public boolean consumirBateria(double distancia) {
        double bat = 0;
        if (distancia >= 0) {
            // Fórmula per reduir la bateria segons l'autonomia màxima
            bat = bateria - ((distancia / AUTONOMIA) * 100);
        }
        if (bat > 0) {
            bateria = bat;
        }
        return false;
    }

    /**
     * @pre Cert.
     * @post Retorna el nivell de bateria en percentatge (0% - 100%).
     */
    public double obtenirBateria() {
        return this.bateria;
    }

    /**
     * @pre Cert.
     * @post Retorna true si la bateria està per sota del 20%, false en cas
     * contrari.
     *
     * @return true si la bateria és baixa, false si encara és suficient.
     */
    public boolean bateriaBaixa() {
        return this.bateria < 20;
    }

    /**
     * @pre novaUbicacio != null i distancia >= 0.
     * @post El vehicle es desplaça a la nova ubicació i la seva bateria es
     * redueix en funció de la distància recorreguda.
     *
     * @param novaUbicacio Lloc on es mourà el vehicle.
     * @param distancia Distància recorreguda per arribar a la nova ubicació.
     */
    public boolean moure(Lloc novaUbicacio, double distancia) {
        if (consumirBateria(distancia)) {
            this.ubicacio = novaUbicacio;
            return true;
        }

        return false;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle està carregant, false en cas contrari.
     */
    public boolean esCarregant() {
        return this.carregant;
    }

}

/**
 * @pre Cert.
 * @post Assigna una petició al conductor.
 */
/*          private Peticio peticio;
    public void assignarPeticio(Peticio p){
        this.peticio=p;
    };*/
