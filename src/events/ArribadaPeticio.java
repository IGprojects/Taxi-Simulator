package events;

import java.time.LocalTime;

import core.Peticio;
import core.Simulador;

public class ArribadaPeticio extends Event {
    private Peticio peticio;

    public ArribadaPeticio(LocalTime temps, Peticio p) {
        super(temps);
        this.peticio = p;
    }

    @Override
    public void executar(Simulador simulador) {
        simulador.afegirPeticio(peticio);
        // Potser planificar servei, generar següent esdeveniment, etc.
    }
}