package events;

import java.time.LocalTime;

import core.Ruta;
import core.Simulador;
import core.Vehicle;
public abstract class Event implements Comparable<Event> {
    protected LocalTime temps;

    public Event(LocalTime temps) {
        this.temps = temps;
    }

    public LocalTime getTemps() {
        return temps;
    }

    @Override
    public int compareTo(Event altre) {
        return this.temps.compareTo(altre.temps);
    }

    public abstract void executar(Simulador simulador);
}

