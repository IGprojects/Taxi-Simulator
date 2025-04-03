package events;

import java.util.Date;

import core.Peticio;
import core.Simulador;

public class ArribadaPeticio extends Event {
    private Peticio peticio;

    public ArribadaPeticio(Date temps, Peticio p) {
        super(temps);
        this.peticio = p;
    }

    @Override
    public void executar(Simulador simulador) {
        simulador.afegirPeticio(peticio);
        // Potser planificar servei, generar següent esdeveniment, etc.
    }
}