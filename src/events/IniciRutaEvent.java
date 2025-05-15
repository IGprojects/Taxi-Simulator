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
    private Conductor conductor; /// < Conductor que realitza l'esdeveniment
    private Ruta ruta; /// < Ruta que es realitza
    private Vehicle vehicle; /// < Vehicle que es fa servir per realitzar la ruta

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
        System.out.println();

    }

    /**
     * @pre Cert.
     * @post El vehicle del conductor inicia la ruta i es pinta el missatge per
     *       pantalla. El conductor queda ocupat durant la ruta.
     * @param simulador Simulador on es realitza l'esdeveniment
     */
    @Override
    public void executar(Simulador simulador) {
        String missatge = "";
        if (ruta.isRutaCarrega()) {
            missatge = "[" + temps + "] El conductor " + conductor.getId()
                    + " inicia la ruta per anar a carregar.";

        } else {
            missatge = "[" + temps + "] El conductor " + conductor.getId()
                    + " inicia la ruta per fer la petició.";
        }
        System.out.println(missatge);
        simulador.pintarMissatge(missatge);

        conductor.setOcupat(true);
        conductor.executarRuta(ruta, vehicle, simulador);
    }

}
