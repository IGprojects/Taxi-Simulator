package events;

import java.time.LocalTime;

import core.Simulador;
import core.Vehicle;

/**
 * @class FiCarregaEvent
 * @brief Representa un esdeveniment que indica el final de la càrrega d'un vehicle.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class FiCarregaEvent extends Event {
    private Vehicle vehicle;

    public FiCarregaEvent(LocalTime temps, Vehicle vehicle) {
        super(temps);
        this.vehicle = vehicle;
    }

    @Override
    public void executar(Simulador simulador) {
        vehicle.carregarBateria(true); // carrega total
        System.out.println("[" + temps + "] Càrrega finalitzada del vehicle " + vehicle.getId());
    }
}
