package events;

import java.time.LocalTime;

import core.Conductor;
import core.Lloc;
import core.Simulador;

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
    /// < Conductor que realitza l'esdeveniment
    private Lloc desti;
    /// < Lloc on es recolliran els passatgers
    private int passatgersRecollits = 0;

    /// < Nombre de passatgers recollits

    public RecollirPassatgersEvent(LocalTime temps, Conductor conductor, Lloc desti, int passatgersRecollits) {
        super(temps);
        this.conductor = conductor;
        this.desti = desti;
        this.passatgersRecollits = passatgersRecollits;
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor recull els passatgers especificats i pinta
     *       el missatge per pantalla.
     */
    @Override
    public void executar(Simulador simulador) {
        if ((conductor.getVehicle().passatgersActuals() + passatgersRecollits) <= conductor.getVehicle()
                .getMaxPassatgers()) {
            conductor.getVehicle().afegirPassatgers(passatgersRecollits);
            String missatge = "[" + temps + "] El conductor " + conductor.getId() + " ha recollit passatgers al lloc "
                    + desti.obtenirId();
            String missatge2 = "(Passatgers actuals: " + conductor.getVehicle().getPassatgersActuals() + ")";

            System.out.println(missatge);
            simulador.pintarMissatge(missatge);
            simulador.pintarMissatge(missatge2);
        }

    }

    // GETTERS
    /**
     * Obté el conductor associat a l'esdeveniment de recollida de passatgers
     *
     * @return objecte Conductor que realitza l'acció
     * @throws IllegalStateException si no hi ha conductor assignat
     */
    public Conductor getConductor() {
        if (this.conductor == null) {
            throw new IllegalStateException("No hi ha conductor assignat a l'esdeveniment de recollida");
        }
        return this.conductor;
    }

    /**
     * Obté la ubicació on es recullen els passatgers
     *
     * @return objecte Lloc destí de la recollida
     * @throws IllegalStateException si no hi ha destí assignat
     */
    public Lloc getDesti() {
        if (this.desti == null) {
            throw new IllegalStateException("No hi ha ubicació de destí assignada");
        }
        return this.desti;
    }

    /**
     * Obté el nombre de passatgers recollits
     *
     * @return enter amb el nombre de passatgers (valor inicial 0)
     */
    public int getPassatgersRecollits() {
        return this.passatgersRecollits;
    }
}
