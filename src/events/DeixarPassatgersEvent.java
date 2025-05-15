package events;

import java.time.LocalTime;

import core.Conductor;
import core.Lloc;
import core.Simulador;

/**
 * @class DeixarPassatgersEvent
 * @brief Representa un esdeveniment que indica que un vehicle comença a deixar
 *        passatgers.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.15
 */
public class DeixarPassatgersEvent extends Event {

    private Conductor conductor; /// < Conductor que realitza l'esdeveniment
    private Lloc desti; /// < Lloc on es deixaran els passatgers
    private int passatgersDeixats; /// < Nombre de passatgers deixats

    public DeixarPassatgersEvent(LocalTime temps, Conductor conductor, Lloc desti, int passatgersDeixats) {
        super(temps);
        this.conductor = conductor;
        this.desti = desti;
        this.passatgersDeixats = passatgersDeixats;
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor deixa els passatgers especificats i pinta el missatge per pantalla.
     * @param simulador Simulador on es realitza l'esdeveniment
     */
    @Override
    public void executar(Simulador simulador) {
        conductor.getVehicle().alliberarPassatgersConcret(passatgersDeixats);
        String missatge = "[" + temps + "] El conductor " + conductor.getId() + " ha deixat els passatgers al destí: "
                + desti.obtenirId();
        String missatge2 = " (Passatgers actuals: " + conductor.getVehicle().getPassatgersActuals() + ")";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        simulador.pintarMissatge(missatge2);
    }
}
