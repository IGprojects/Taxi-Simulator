package events;

import java.time.LocalTime;

import core.Simulador;

/**
 * @class Event
 * @brief Representa un esdeveniment en la simulació.
 * @details Cada esdeveniment té un temps associat i pot ser comparat amb altres
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
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
