package events;

import java.time.LocalTime;

import core.Conductor;
import core.Lloc;
import core.Peticio;
import core.Simulador;
import core.Vehicle;

/**
 * @class RecollirPassatgersEvent
 * @brief Representa un esdeveniment que indica que un vehicle comença a
 *        recollir passatgers.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.15
 */
public class RecollirPassatgersEvent extends Event {

    private Conductor conductor;
    private Lloc desti;
    private int passatgersRecollits = 0;

    public RecollirPassatgersEvent(LocalTime temps, Conductor conductor, Lloc desti, int passatgersRecollits) {
        super(temps);
        this.conductor = conductor;
        this.desti = desti;
        this.passatgersRecollits = passatgersRecollits;
    }

    @Override
    public void executar(Simulador simulador) {
        conductor.getVehicle().afegirPassatgers(passatgersRecollits);
        String missatge = "[" + temps + "] El conductor " + conductor.getId() + " ha recollit passatgers al lloc: "
                + desti.obtenirId();
        String missatge2 = "(Passatgers actuals: " + conductor.getVehicle().getPassatgersActuals() + ")";

        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        simulador.pintarMissatge(missatge2);

    }
}
