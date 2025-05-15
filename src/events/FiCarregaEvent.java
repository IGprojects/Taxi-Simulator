package events;

import java.time.LocalTime;

import core.Conductor;
import core.ConductorVoraç;
import core.Simulador;

/**
 * @class FiCarregaEvent
 * @brief Representa un esdeveniment que indica el final de la càrrega d'un
 *        vehicle.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class FiCarregaEvent extends Event {
    private Conductor conductor; /// < Conductor que realitza l'esdeveniment

    public FiCarregaEvent(LocalTime temps, Conductor conductor) {
        super(temps);
        this.conductor = conductor;
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor acaba la càrrega i es pinta el missatge per
     *       pantalla. El conductor queda lliure i s'intenten assignar les peticions
     */
    @Override
    public void executar(Simulador simulador) {
        conductor.getVehicle().carregarBateria(true);
        conductor.setOcupat(false);
        String missatge = "[" + temps + "] Càrrega finalitzada del vehicle " + conductor.getVehicle().getId();
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);

        if (simulador.hiHaPeticions())
            if (conductor instanceof ConductorVoraç)
                simulador.assignarPeticionsVoraç();
            else
                simulador.assignarPeticionsPlan();
    }
}
