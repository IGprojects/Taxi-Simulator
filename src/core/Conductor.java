package core;

import java.time.LocalTime;

/**
 * @class Conductor
 * @brief Defineix la classe pare dels tipus de conductors
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public abstract class Conductor {
    protected int id; /// < Identificador del conductor.
    protected String nom; /// < Nom del conductor.
    private Peticio peticio;// Peticio actual del contuctor
    protected Vehicle vehicle;/// < Vehicle que condueix el conductor
    protected boolean ocupat; /// < Indica si el conductor està ocupat o no.

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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupat() {
        return ocupat;
    }

    public void setOcupat(boolean ocupat) {
        this.ocupat = ocupat;
    }

    public abstract Ruta planificarRuta(Peticio peticio, Mapa mapa);

    public abstract boolean teBateria(double distancia, Simulador simulador, Mapa mapa, LocalTime horaInici,
            LocalTime horaActual);

    public abstract boolean potServirPeticio(int nombrePassatgers);
}
