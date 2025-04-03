package events;

import java.util.Date;

import core.Simulador;
public abstract class Event implements Comparable<Event> {
    protected Date temps;

    public Event(Date temps) {
        this.temps = temps;
    }

    public Date getTemps() {
        return temps;
    }

    @Override
    public int compareTo(Event altre) {
        return this.temps.compareTo(altre.temps);
    }

    public abstract void executar(Simulador simulador);
}

