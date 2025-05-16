package core;

import java.time.LocalTime;

/**
 * @class Conductor
 * @brief Defineix la classe pare dels tipus de conductors
 *
 * @author Anouar El Barkouki Hitach
 * @version 2025.03.04
 */
public abstract class Conductor {
    protected int id; /// < Identificador del conductor.
    protected String nom; /// < Nom del conductor.
    protected Vehicle vehicle;/// < Vehicle que condueix el conductor
    protected boolean ocupat; /// < Indica si el conductor està ocupat o no.

    /**
     * @pre Cert.
     * @post Crea un conductor amb un id, nom i vehicle especificats.
     *
     * @param id      Identificador del conductor.
     * @param nom     Nom del conductor.
     * @param vehicle Vehicle que condueix el conductor.
     */

    public Conductor(int id, String nom, Vehicle vehicle) {
        this.id = id;
        this.nom = nom;
        this.vehicle = vehicle;
        this.ocupat = false;
    }

    /**
     * @pre Cert.
     * @post Retorna si el conductor està disponible.
     */
    public abstract void executarRuta(Ruta ruta, Vehicle vehicle, Simulador simulador);

    /**
     * @pre Cert.
     * @post Retorna el vehicle del conductor.
     * 
     * @return Vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * @pre Cert.
     * @post Retorna la id del conductor.
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * @pre Cert.
     * @post Retorna si el conductor està ocupat o no.
     * @return String
     */
    public boolean isOcupat() {
        return ocupat;
    }

    /**
     * @pre Cert.
     * @post Assigna si el conductor està ocupat o no.
     * 
     */
    public void setOcupat(boolean ocupat) {
        this.ocupat = ocupat;
    }

    /**
     * @brief Comprova si el vehicle té bateria suficient per fer la distància
     *        indicada. Si no en té, intenta planificar una ruta cap al pàrquing més
     *        proper per carregar.
     *
     * @param distancia  La distància que s’ha de recórrer per completar la petició.
     * @param simulador  El simulador on s'afegiran esdeveniments si cal carregar.
     * @param mapa       El mapa que permet calcular rutes i distàncies.
     * @param horaInici  L’hora d’inici de la simulació (referència per calcular
     *                   horaris).
     * @param horaActual L’hora actual del simulador, per planificar nous
     *                   esdeveniments.
     * @return true si el vehicle té bateria suficient per fer el trajecte, false si
     *         no i ha de carregar.
     *
     * @pre El vehicle ha d’estar inicialitzat i ubicat a un lloc vàlid dins el
     *      mapa.
     * @post Si no té bateria, pot iniciar una ruta cap a un pàrquing privat per
     *       carregar. Si no hi ha pàrquing, es mostra un missatge.
     */
    public abstract boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual);

    /**
     * @pre Cert.
     * @post Comprova si el vehicle compleix les condicions per fer la petició.
     * @param nombrePassatgers
     * @return true si el vehicle pot fer la petició, false en cas contrari.
     */
    public abstract boolean potServirPeticio(int nombrePassatgers);
}
