package events;

import java.time.LocalTime;
import core.Simulador;
import core.Conductor;

/**
 * @class FiRutaEvent
 * @brief Representa un esdeveniment que indica el final d'una ruta per un conductor.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
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
