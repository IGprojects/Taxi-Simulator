package core;

import java.util.List;

/**
 * @class Parquing
 * @brief Defineix la localitzacio amb pàrquing
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Parquing extends Lloc {

    private List<PuntCarrega> puntsCargaPrivats; /// < Nombre de punts de càrrega ràpida disponibles.
    private List<PuntCarrega> puntsCargaPublics; /// < Nombre de punts de càrrega lenta disponibles.
    private int capacitatMaxima, vehiclesActuals; /// < Capacitat màxima de vehicles en el pàrquing.

    public Parquing(int id, int capacitatMaxima, List<PuntCarrega> puntsCargaPublics,
            List<PuntCarrega> puntsCargaPrivats) {
        super(id);
        this.puntsCargaPublics = puntsCargaPublics;
        this.puntsCargaPrivats = puntsCargaPrivats;
        this.capacitatMaxima = capacitatMaxima;
        this.vehiclesActuals = 0;
    }

    public PuntCarrega puntCarregaPublicDisponible() {

        for (PuntCarrega punt : puntsCargaPublics) {
            if (!punt.isOcupat()) {
                return punt;
            }
        }
        return null; // No hi ha punts de càrrega disponibles
    }

    public boolean esCarregadorPrivat(int id) {
        return id == this.id;
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
     * @post Retorna el nombre de punts de càrrega privats disponibles.
     *
     * @return Nombre de punts de càrrega privats disponibles.
     */
    public int obtenirPuntsCarregaPrivats() {
        return puntsCargaPrivats.size();
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de punts de càrrega públics disponibles.
     *
     * @return Nombre de punts de càrrega públics disponibles.
     */
    public int obtenirPuntsCarregaPublics() {
        return puntsCargaPublics.size();
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de places de pàrquing disponibles.
     *
     * @return Nombre de places de pàrquing disponibles.
     */
    public int obtenirPlaces() {
        return capacitatMaxima;
    }

    public int obtenirId() {
        return id;
    }
}
