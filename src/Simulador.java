import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @class Simulador
 * @brief Classe que gestiona l'execució de la simulació.
 * @details Controla els vehicles, conductors i peticions, processant-les en
 *          funció del temps.
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Simulador {

    private List<Vehicle> vehicles; ///< Vehicles disponibles per la simulació.
    private List<Conductor> conductors; ///< Conductors disponibles per la simulació.
    private List<Peticio> peticions; ///< Peticions pendents de servei.
    private Date horaInici; ///< Hora d'inici de la simulació.
    private Date horaFi; ///< Hora de finalització de la simulació.
    private Mapa mapa; ///< Mapa de la ciutat.

    public Simulador(Date horaInici, Date horaFi, Mapa mapa) {
        this.vehicles = new ArrayList<Vehicle>();
        this.conductors = new ArrayList<Conductor>();
        this.peticions = new ArrayList<Peticio>();
        this.horaInici = horaInici;
        this.horaFi = horaFi;
        this.mapa = mapa;
    }

    /**
     * @pre v != null
     * @post El vehicle s'afegeix a la llista de vehicles disponibles per la
     *       simulació.
     *
     * @param v Vehicle a afegir.
     */
    public void afegirVehicle(Vehicle v) {
        vehicles.add(v);
    }

    /**
     * @pre c != null
     * @post El conductor s'afegeix a la llista de conductors disponibles per la
     *       simulació.
     *
     * @param c Conductor a afegir.
     */
    public void afegirConductor(Conductor c) {
        conductors.add(c);
    }

    /**
     * @pre p != null
     * @post La petició es registra i queda pendent d’assignació a un vehicle.
     *
     * @param p Petició a afegir.
     */
    public void afegirPeticio(Peticio p) {
        peticions.add(p);
    }

    /**
     * @pre cert
     * @post Es genera una petició de trasllat amb dades aleatòries i s'afegeix a la
     *       llista de peticions.
     */
    public void afegirPeticioAleatoria() {
    }

    /**
     * @pre cert
     * @post Inicia l'execució de la simulació.
     * 
     */
    public void iniciar() {
        while (!peticions.isEmpty()) {
            double distMin = 0;
            Vehicle vMin = null;
            for (Vehicle v : vehicles) {
                if (!v.estaOcupat()) {
                    double dist = v.distanciaFins(peticioActual().getOrigen());
                    if (dist < distMin) {
                        distMin = dist;
                        vMin = v;
                    }
                }

            }
            if (vMin != null) {
                vMin.moure(peticioActual().obtenirOrigen(), distMin);
                vMin.carregarBateria(100);
                peticioServida();
            }
        }
        finalitzar();
    }

    /**
     * @pre !peticions.isEmpty()
     * @post S'elimina la petició ja servida
     * 
     */
    private Peticio peticioActual() {
        return peticions.get(0);

    }

    /**
     * @pre !peticions.isEmpty()
     * @post S'elimina la petició ja servida
     * 
     */
    private void peticioServida() {
        peticions.remove(0);

    }

    /**
     * @pre cert
     * @post Tanca la simulació i mostra un resum dels resultats.
     */
    private void finalitzar() {

    }
}
