package events;

import java.time.LocalTime;

import core.Conductor;
import core.Simulador;
import core.Vehicle;

/**
 * @class CarregarBateriaEvent
 * @brief Representa un esdeveniment que indica que un vehicle comença a carregar la bateria.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class CarregarBateriaEvent extends Event {

    private Vehicle vehicle;
    private double duracioCarregaMinuts;
    private Conductor conductor;

    public CarregarBateriaEvent(LocalTime temps, Vehicle vehicle, double duracioCarregaMinuts, Conductor conductor) {
        super(temps);
        this.vehicle = vehicle;
        this.duracioCarregaMinuts = duracioCarregaMinuts;
        this.conductor = conductor;
    }

    @Override
    public void executar(Simulador simulador) {
        String missatge = "[" + temps + "] El vehicle " + vehicle.getId() + " comença a carregar la bateria.";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        vehicle.esCarregant();
        conductor.setOcupat(false);
        // Programem final de la càrrega
        LocalTime fiCarrega = temps.plusMinutes((long) duracioCarregaMinuts);
        simulador.afegirEsdeveniment(new FiCarregaEvent(fiCarrega, conductor));
    }
}
