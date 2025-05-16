package events;

import java.time.LocalTime;

import core.Conductor;
import core.ConductorVorac;
import core.Ruta;
import core.Simulador;

/**
 * @class FiRutaEvent
 * @brief Representa un esdeveniment que indica el final d'una ruta per un
 * conductor.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class FiRutaEvent extends Event {

    private Conductor conductor;
    /// < Conductor que realitza l'esdeveniment
    private Ruta ruta;

    /// < Ruta que es realitza

    public FiRutaEvent(LocalTime temps, Conductor Conductor, Ruta ruta) {
        super(temps);
        this.conductor = Conductor;
        if (ruta != null) {
            this.ruta = ruta;
        } else {
            System.out.println("Ruta no vàlida");
        }
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor acaba la ruta i es pinta el missatge per
     * pantalla. El conductor queda lliure i s'intenten assignar les peticions
     * @param simulador Simulador on es realitza l'esdeveniment
     */
    @Override
    public void executar(Simulador simulador) {
        conductor.setOcupat(false);

        if (simulador.hiHaPeticions()) {
            if (conductor instanceof ConductorVorac) {
                simulador.assignarPeticionsvorac(); 
            }else {
                simulador.assignarPeticionsPlan();
            }
        }

        String missatge = "[" + temps + "] Conductor " + conductor.getId() + " ha acabat la ruta.";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
    }

    /**
     * Obtiene el conductor asociado al evento
     *
     * @return Objeto Conductor que realiza la acción
     * @throws IllegalStateException si el conductor no está asignado
     */
    public Conductor getConductor() {
        if (this.conductor == null) {
            throw new IllegalStateException("No hay conductor asignado al evento");
        }
        return this.conductor;
    }
}
