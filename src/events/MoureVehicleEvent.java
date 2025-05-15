package events;

import java.time.LocalTime;

import core.Simulador;
import core.Vehicle;
import core.Cami;
import core.Lloc;

/**
 * @class MoureVehicleEvent
 * @brief Representa un esdeveniment de moviment d'un vehicle.
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
        this.distancia = distancia;
    }

    @Override
    public void executar(Simulador simulador) {
        String missatge =   "[" + temps + "] Vehicle " + vehicle.getId() + " es mou de " + origen.obtenirId() + " a "
                        + desti.obtenirId() + ".";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        vehicle.moure(desti, distancia);

        // Notificar al MapPanel per pintar aquest tram
        if (simulador.getMapPanel() != null) {
            simulador.getMapPanel().animarCami(new Cami(origen, desti, distancia, 0), vehicle);
        }
    }

}
