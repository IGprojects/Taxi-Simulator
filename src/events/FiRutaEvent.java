package events;

import java.time.LocalTime;
import core.Simulador;
import core.Conductor;
import core.ConductorVoraç;
import core.Ruta;

/**
 * @class FiRutaEvent
 * @brief Representa un esdeveniment que indica el final d'una ruta per un
 *        conductor.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class FiRutaEvent extends Event {
    private Conductor conductor;
    private Ruta ruta;

    public FiRutaEvent(LocalTime temps, Conductor Conductor, Ruta ruta) {
        super(temps);
        this.conductor = Conductor;
        if (ruta != null) {
            this.ruta = ruta;
        } else {
            System.out.println("Ruta no vàlida");
        }
    }

    @Override
    public void executar(Simulador simulador) {
        conductor.setOcupat(false);
        if (!ruta.isEsRutaCarrega())
            conductor.getVehicle().alliberarPassatgers();

        if (simulador.hiHaPeticions())
            if (conductor instanceof ConductorVoraç)
                simulador.assignarPeticionsVoraç();
            else
                simulador.assignarPeticionsPlan();

        String missatge = "[" + temps + "] Conductor " + conductor.getId() + " ha acabat la ruta.";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
    }
}
