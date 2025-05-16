package events;

import java.time.LocalTime;

import core.Conductor;
import core.Simulador;
import core.Vehicle;

/**
 * @class CarregarBateriaEvent
 * @brief Representa un esdeveniment que indica que un vehicle comença a
 * carregar la bateria.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class CarregarBateriaEvent extends Event {

    private Vehicle vehicle;
    /// < Vehicle que es carrega
    private double duracioCarregaMinuts;
    /// < Duració de la càrrega en minuts
    private Conductor conductor;

    /// < Conductor que realitza l'esdeveniment

    public CarregarBateriaEvent(LocalTime temps, Vehicle vehicle, double duracioCarregaMinuts, Conductor conductor) {
        super(temps);
        this.vehicle = vehicle;
        this.duracioCarregaMinuts = duracioCarregaMinuts;
        this.conductor = conductor;
    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor comença a carregar la bateria i es pinta
     * el missatge per pantalla. El vehicle queda ocupat durant la càrrega. Es
     * crea un nou esdeveniment FiCarregaEvent per indicar el final de la
     * càrrega.
     * @param simulador Simulador on es realitza l'esdeveniment
     */
    @Override
    public void executar(Simulador simulador) {
        String missatge = "[" + temps + "] El vehicle " + vehicle.getId() + " comença a carregar la bateria.";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        vehicle.esCarregant();
        // Programem final de la càrrega
        LocalTime fiCarrega = temps.plusMinutes((long) duracioCarregaMinuts);
        simulador.afegirEsdeveniment(new FiCarregaEvent(fiCarrega, conductor));
    }

    //GETTERS
    /**
     * Obtiene el vehículo que se está cargando
     *
     * @return El objeto Vehicle asociado a la carga
     */
    public Vehicle getVehicle() {
        return this.vehicle;
    }

    /**
     * Obtiene la duración de la carga en minutos
     *
     * @return Duración en minutos (valor double)
     */
    public double getDuracioCarregaMinuts() {
        return this.duracioCarregaMinuts;
    }

    /**
     * Obtiene el conductor que realiza el evento de carga
     *
     * @return El objeto Conductor asociado
     */
    public Conductor getConductor() {
        return this.conductor;
    }
}
