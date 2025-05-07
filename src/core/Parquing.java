package core;

/**
 * @class Parquing
 * @brief Defineix la localitzacio amb pàrquing
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Parquing extends Lloc {

    private int puntscargaRapida; /// < Nombre de punts de càrrega ràpida disponibles.
    private int puntscargaLenta; /// < Nombre de punts de càrrega lenta disponibles.

    public Parquing(int id, int capacitatMaxima, int puntscargaRapida, int puntscargaLenta) {
        super(id, capacitatMaxima);
        this.puntscargaRapida = puntscargaRapida;
        this.puntscargaLenta = puntscargaLenta;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de punts de càrrega ràpida disponibles.
     *
     * @return Nombre de punts de càrrega ràpida disponibles.
     */
    public int obtenirPuntCargaRapida() {
        return puntscargaRapida;
    }

    /**
     * @pre Cert.
     * @post Retorna el nombre de punts de càrrega lenta disponibles.
     *
     * @return Nombre de punts de càrrega lenta disponibles.
     */
    public int obtenirPuntCargaLenta() {
        return puntscargaLenta;
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
}
