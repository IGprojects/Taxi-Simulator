package core;

/**
 * @class Vehicle
 * @brief Defineix el vehicle i les seves característiques
 *
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public class Vehicle {

    final int MAXPASSATGERS; /// < Nombre màxim de passatgers que pot transportar el vehicle.
    final int AUTONOMIA; /// < Autonomia màxima del vehicle en quilòmetres.
    final double TEMPSCARGARAPIDA; /// < Temps en minuts per carregar la bateria amb carga ràpida.
    final double TEMPSCARGALENTA; /// < Temps en minuts per carregar la bateria amb carga lenta.

    private int id; /// < Identificador del vehicle.
    private Lloc ubicacio; /// < Lloc on es troba actualment el vehicle.
    private double bateria; /// < Nivell actual de bateria del vehicle (0% - 100%).
    private int percentatgeCarrega; /// < Percentatge de càrrega del vehicle (0% - 100%).
    private int numPassatgers; /// < Nombre de passatgers actuals del vehicle.
    private boolean carregant; /// < Indica si el vehicle està carregant.

    /**
     * Constructor de la classe Vehicle.
     *
     * @param ubicacio La ubicació inicial del vehicle.
     */
    public Vehicle(int id, Lloc ubicacio, int maxpassatgers, int autonomia, double tempsCargaLenta,
            double tempsCargaRapida) {
        this.id = id;
        this.ubicacio = ubicacio;
        this.bateria = autonomia; // La bateria comença plena
        this.numPassatgers = 0; // El vehicle comença buit
        this.AUTONOMIA = autonomia;
        this.MAXPASSATGERS = maxpassatgers;
        this.TEMPSCARGALENTA = tempsCargaLenta;
        this.TEMPSCARGARAPIDA = tempsCargaRapida;
        this.carregant = false; // El vehicle no està carregant inicialment
        this.percentatgeCarrega = 100; // Bateria al 100% inicialment
        percentatgeCarrega = 25;
    }

    public int getId() {
        return id;
    }

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle té bateria (>0%), altrament false.
     * @return true si el vehicle té bateria disponible, false si està esgotada.
     */
    public boolean teBateria(double km, boolean voraç) {
        if (voraç) {
            double batBaixar = (int) ((km / AUTONOMIA) * 100);
            return percentatgeCarrega - batBaixar > 20;
        }
        return true;
        // return this.bateria > km;
    }

    /**
     * @pre Cert.
     * @post Retorna la ubicacio actual del vehicle
     * @return Lloc on es troba actualment el conductor
     */
    public Lloc getUbicacioActual() {
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
     *      especificats al vehicle si hi ha espai suficient.
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
     *                    100]).
     */
    public void carregarBateria(boolean voraç) {
        bateria = voraç ? 80 : 100; // Carrega ràpida al 80% o lenta al 100%
                percentatgeCarrega = voraç ? 80 : 100; // Carrega ràpida al 80% o lenta al 100%
 
        carregant = false; // El vehicle ja no està carregant

    }

    /**
     * @pre bateria >0.
     * @post La bateria es redueix en funció de la distància recorreguda.
     *
     * @param distancia Distància en quilòmetres que el vehicle ha recorregut.
     */
    public boolean consumirBateria(double distancia) {
        System.out.println("-----------");
        System.out.println("Distancia: " + distancia);
        double bat = 0;
        if (distancia >= 0) {
            System.out.println("Bateria abans: " + percentatgeCarrega);
            // Fórmula per reduir la bateria segons l'autonomia màxima
            bateria -= distancia;
            percentatgeCarrega -= (int) ((distancia / AUTONOMIA) * 100);
            System.out.println("Bateria ara: " + percentatgeCarrega);
            System.out.println("-----------");
        }
        return false;
    }

    /**
     * @pre Cert.
     * @post Retorna el nivell de bateria en percentatge (0% - 100%).
     */
    public double obtenirBateria() {
        return percentatgeCarrega;
        // return this.bateria;
    }

    /**
     * @pre Cert.
     * @post Retorna true si la bateria està per sota del 20%, false en cas
     *       contrari.
     *
     * @return true si la bateria és baixa, false si encara és suficient.
     */
    public boolean bateriaBaixa() {
        return this.bateria < 20;
    }

    /**
     * @pre novaUbicacio != null i distancia >= 0.
     * @post El vehicle es desplaça a la nova ubicació i la seva bateria es
     *       redueix en funció de la distància recorreguda.
     *
     * @param novaUbicacio Lloc on es mourà el vehicle.
     * @param distancia    Distància recorreguda per arribar a la nova ubicació.
     */
    public void moure(Lloc novaUbicacio, double distancia) {
        consumirBateria(distancia);
        this.ubicacio = novaUbicacio;

    }

    /**
     * @pre Cert.
     * @post Retorna true si el vehicle està carregant, false en cas contrari.
     */
    public boolean esCarregant() {
        return this.carregant;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre màxim de passatgers que pot transportar el vehicle.
     *
     * @param MAXPASSATGERS Nombre màxim de passatgers que pot transportar el
     *                      vehicle.
     * @return El nombre màxim de passatgers que pot transportar el vehicle.
     */
    public int getMaxPassatgers() {
        return MAXPASSATGERS;
    }

}