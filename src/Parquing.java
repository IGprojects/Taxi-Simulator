
/**
 * @class Parquing
 * @brief Defineix la localitzacio amb pàrquing
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Parquing extends Lloc{

    private int puntcargaRapida; /// < Nombre de punts de càrrega ràpida disponibles.
    private int puntcargaLenta; /// < Nombre de punts de càrrega lenta disponibles.
    private int places; /// < Nombre de places de pàrquing disponibles.

    /**
     * Constructor de la classe Parquing.
     *
     * @param puntcargaRapida Nombre de punts de càrrega ràpida disponibles.
     * @param puntcargaLenta  Nombre de punts de càrrega lenta disponibles.
     * @param places          Nombre de places de pàrquing disponibles.
     */
    public Parquing(int puntcargaRapida, int puntcargaLenta, int places,Lloc _lloc,) {
        this.puntcargaRapida = puntcargaRapida;
        this.puntcargaLenta = puntcargaLenta;
        this.places = places;
        this.
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de punts de càrrega ràpida disponibles.
     *
     * @return Nombre de punts de càrrega ràpida disponibles.
     */
    public int obtenirPuntCargaRapida() {
        return puntcargaRapida;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de punts de càrrega lenta disponibles.
     *
     * @return Nombre de punts de càrrega lenta disponibles.
     */
    public int obtenirPuntCargaLenta() {
        return puntcargaLenta;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de places de pàrquing disponibles.
     *
     * @return Nombre de places de pàrquing disponibles.
     */
    public int obtenirPlaces() {
        return places;
    }
}
