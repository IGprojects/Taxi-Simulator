package events;

import java.time.LocalTime;

import core.Simulador;
import core.Vehicle;
import core.Lloc;
import core.Ruta;

/**
 * @class Event
 * @brief Representa un esdeveniment en la simulació.
 * @details Cada esdeveniment té un temps associat i pot ser comparat amb altres
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class MoureVehicleEvent extends Event {
    private Vehicle vehicle;
    private Lloc origen;
    private Lloc desti;
    private double distancia; // Distància entre origen i destí
    public MoureVehicleEvent(LocalTime temps, Vehicle vehicle, Lloc origen, Lloc desti, double distancia) {
        super(temps);
        this.vehicle = vehicle;
        this.origen = origen;
        this.desti = desti;
    }

    @Override
    public void executar(Simulador simulador) {
        System.out.println("[" + temps + "] Vehicle " + " es mou de " + " a " );
        
        // Actualitzar la posició del vehicle
        vehicle.moure(desti, distancia);
    }

}
