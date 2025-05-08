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

    public Parquing(int id, int capacitatMaxima, List<PuntCarrega> puntsCargaPublics,
            List<PuntCarrega> puntsCargaPrivats) {
        super(id, capacitatMaxima);
        this.puntsCargaPublics = puntsCargaPublics;
        this.puntsCargaPrivats = puntsCargaPrivats;
    }

    public PuntCarrega puntCarregaPublicDisponible(){

        for (PuntCarrega punt : puntsCargaPublics) {
            if (!punt.isOcupat()) {
                return punt;
            }
        }
        return null; // No hi ha punts de càrrega disponibles
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
}
