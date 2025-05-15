package events;

import java.time.LocalTime;

import core.Cami;
import core.Lloc;
import core.Simulador;
import core.Vehicle;

/**
 * @class MoureVehicleEvent
 * @brief Representa un esdeveniment de moviment d'un vehicle.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class MoureVehicleEvent extends Event {

    private Vehicle vehicle; /// < Vehicle que es mou
    private Lloc origen; /// < Lloc d'origen del vehicle
    private Lloc desti; /// < Lloc de destí del vehicle
    private double distancia; /// < Distància entre l'origen i el destí

    public MoureVehicleEvent(LocalTime temps, Vehicle vehicle, Lloc origen, Lloc desti, double distancia) {
        super(temps);
        this.vehicle = vehicle;
        this.origen = origen;
        this.desti = desti;
        this.distancia = distancia;
    }

    /**
     * @pre Cert.
     * @post El vehicle es mou de l'origen al destí, es pinta el camí i es pinta el missatge per pantalla.
     * @param simulador Simulador on es realitza l'esdeveniment
     */
    @Override
    public void executar(Simulador simulador) {
        String missatge = "[" + temps + "] Vehicle " + vehicle.getId() + " es mou de " + origen.obtenirId() + " a "
                + desti.obtenirId() + ".";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);
        vehicle.moure(desti, distancia);

        // Notificar al MapPanel per pintar aquest tram
        if (simulador.getMapPanel() != null) {
            simulador.getMapPanel().animarCami(new Cami(origen, desti, distancia, 0), vehicle);
        }
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public Lloc getOrigen() {
        return this.origen;
    }

    public Lloc getDesti() {
        return this.desti;
    }

    public double getDistancia() {
        return this.distancia;
    }

}
