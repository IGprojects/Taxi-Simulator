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
    protected LocalTime temps; /// < Temps associat a l'esdeveniment

    public Event(LocalTime temps) {
        this.temps = temps;
    }

    /**
     * @pre Cert.
     * @post Retorna el temps associat a l'esdeveniment.
     * @return temps Temps associat a l'esdeveniment.
     */
    public LocalTime getTemps() {
        return temps;
    }

    /**
     * @pre Cert.
     * @post Retorna negatiu si l'esdeveniment actual és anterior a l'altre, positiu si és
     *       posterior i 0 si són iguals.
     */
    @Override
    public int compareTo(Event altre) {
        return this.temps.compareTo(altre.temps);
    }

    /**
     * @pre Cert.
     * @post Executa l'esdeveniment en el simulador.
     * @param simulador
     */
    public abstract void executar(Simulador simulador);
    
}
