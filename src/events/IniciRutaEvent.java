package events;

import java.time.LocalTime;
import core.Simulador;
import core.Vehicle;
import core.Conductor;
import core.Lloc;
import core.Ruta;

/**
 * @class IniciRutaEvent
 * @brief Representa un esdeveniment que indica l'inici d'una ruta per un
 *        conductor.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class IniciRutaEvent extends Event {
    private Conductor conductor;
    private Ruta ruta;
    private Vehicle vehicle;

    public IniciRutaEvent(LocalTime temps, Conductor conductor, Vehicle vehicle, Ruta ruta) {
        super(temps);
        this.conductor = conductor;
        this.ruta = ruta;
        this.vehicle = vehicle;
        if (ruta.isRutaCarrega()) {
            System.out.println("[" + temps + "] IniciRutaEvent: Conductor " + conductor.getId()
                    + " ha planificat la ruta amb vehicle "
                    + vehicle.getId() + " i ha de carregar la bateria.");
            System.out.println("Llocs: ");
            for (Lloc lloc : ruta.getLlocs()) {
                System.out.print(lloc.obtenirId() + " --> ");
            }
        } else {
            System.out.println("[" + temps + "] IniciRutaEvent: Conductor " + conductor.getId()
                    + " ha planificat la ruta amb vehicle "
                    + vehicle.getId() + " per anar a buscar una peticio.");
            System.out.println("Llocs: ");
            for (Lloc lloc : ruta.getLlocs()) {
                System.out.print(lloc.obtenirId() + " --> ");
            }
        }

    }

    @Override
    public void executar(Simulador simulador) {
        String missatge = "[" + temps + "] El conductor " + conductor.getId() + " inicia la ruta.";
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);

        conductor.setOcupat(true);
        conductor.executarRuta(ruta, vehicle, simulador); // <--- passem el simulador
    }

}
