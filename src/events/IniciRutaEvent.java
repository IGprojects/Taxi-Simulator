package events;

import java.time.LocalTime;
import core.Simulador;
import core.Vehicle;
import core.Conductor;
import core.Lloc;
import core.Ruta;

public class IniciRutaEvent extends Event {
    private Conductor conductor;
    private Ruta ruta;
    private Vehicle vehicle;

    public IniciRutaEvent(LocalTime temps, Conductor conductor, Vehicle vehicle, Ruta ruta) {
        super(temps);
        this.conductor = conductor;
        this.ruta = ruta;
        this.vehicle = vehicle;

        System.out.println("[" + temps + "] IniciRutaEvent: Conductor " + conductor.getId() + " inicia la ruta amb vehicle "
                + vehicle.getId());
    }

    @Override
    public void executar(Simulador simulador) {
        System.out.println("[" + temps + "] El conductor inicia la ruta.");
        conductor.executarRuta(ruta, vehicle, simulador); // <--- passem el simulador
    }

}
