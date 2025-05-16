package events;

import java.time.LocalTime;

import core.Conductor;
import core.Lloc;
import core.Simulador;

/**
 * @class DeixarPassatgersEvent
 * @brief Representa un esdeveniment que indica que un vehicle comença a deixar
 * passatgers.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.15
 */
public class DeixarPassatgersEvent extends Event {

    private Conductor conductor;
    /// < Conductor que realitza l'esdeveniment
    private Lloc desti;
    /// < Lloc on es deixaran els passatgers
    private int passatgersDeixats;

    /// < Nombre de passatgers deixats

    public DeixarPassatgersEvent(LocalTime temps, Conductor conductor, Lloc desti, int passatgersDeixats) {
        super(temps);
        this.conductor = conductor;
        this.desti = desti;
        this.passatgersDeixats = passatgersDeixats;
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor deixa els passatgers especificats i pinta
     * el missatge per pantalla.
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

    //GETTERS
    /**
     * Obté el conductor associat a l'esdeveniment de deixar passatgers
     *
     * @return objecte Conductor que realitza l'acció
     * @throws IllegalStateException si no hi ha conductor assignat
     */
    public Conductor getConductor() {
        if (this.conductor == null) {
            throw new IllegalStateException("No hi ha conductor assignat a l'esdeveniment de deixar passatgers");
        }
        return this.conductor;
    }

    /**
     * Obté la ubicació on es deixen els passatgers
     *
     * @return objecte Lloc destí on es deixen els passatgers
     * @throws IllegalStateException si no hi ha destí assignat
     */
    public Lloc getDesti() {
        if (this.desti == null) {
            throw new IllegalStateException("No hi ha ubicació de destí assignada per deixar passatgers");
        }
        return this.desti;
    }

    /**
     * Obté el nombre de passatgers deixats
     *
     * @return enter amb el nombre de passatgers deixats
     */
    public int getPassatgersDeixats() {
        return this.passatgersDeixats;
    }
}
