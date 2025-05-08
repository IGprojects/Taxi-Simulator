package events;

import java.time.LocalTime;
import core.Simulador;
import core.Conductor;

public class FiRutaEvent extends Event {
    private Conductor conductor;

    public FiRutaEvent(LocalTime temps, Conductor Conductor) {
        super(temps);
        this.conductor = Conductor;
    }

    @Override
    public void executar(Simulador simulador) {
        conductor.setOcupat(false);
        System.out.println("[" + temps + "] Conductor " + conductor.getId() + " ha acabat la ruta. Ara està ocupat?: " + conductor.isOcupat() + ".");

    }
}
