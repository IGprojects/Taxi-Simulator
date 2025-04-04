package core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import events.ArribadaPeticio;
import events.Event;

/**
 * @class Simulador
 * @brief Classe que gestiona l'execució de la simulació.
 * @details Controla els vehicles, conductors i peticions, processant-les en
 * funció del temps.
 *
 * @author Grup b9
 * @version 2025.03.04
 */
public class Simulador {

    private List<Vehicle> vehicles;
    /// < Vehicles disponibles per la simulació.
    private List<Conductor> conductors;
    /// < Conductors disponibles per la simulació.
    private List<Peticio> peticions;
    /// < Peticions pendents de servei.
    private Date horaInici;
    /// < Hora d'inici de la simulació.
    private Date horaFi;
    /// < Hora de finalització de la simulació.
    private Date horaActual;
    /// < Hora actual de la simulació.
    private Mapa mapa;
    /// < Mapa de la ciutat.
    private PriorityQueue<Event> esdeveniments = new PriorityQueue<>();

    /// < Esdeveniments programats.

    public Simulador(Date horaInici, Date horaFi, Mapa mapa) {
        this.vehicles = new ArrayList<Vehicle>();
        this.conductors = new ArrayList<Conductor>();
        this.peticions = new ArrayList<Peticio>();
        this.horaInici = horaInici;
        this.horaFi = horaFi;
        this.mapa = mapa;
        this.horaActual = horaInici;

    }

    /**
     * @pre v != null
     * @post El vehicle s'afegeix a la llista de vehicles disponibles per la
     * simulació.
     *
     * @param v Vehicle a afegir.
     */
    public void afegirVehicle(Vehicle v) {
        vehicles.add(v);
    }

    /**
     * @pre c != null
     * @post El conductor s'afegeix a la llista de conductors disponibles per la
     * simulació.
     *
     * @param c Conductor a afegir.
     */
    public void afegirConductor(Conductor c) {
        conductors.add(c);
    }

    /**
     * @pre e != null
     * @post L'esdeveniment s'afegeix a la cua d'esdeveniments programats
     *
     * @param e Esdeveniment a afegir.
     */
    public void afegirEsdeveniment(Event e) {
        esdeveniments.add(e);
    }

    /**
     * @pre p != null
     * @post La petició es registra i queda pendent d’assignació a un vehicle.
     *
     * @param p Petició a afegir.
     */
    public void afegirPeticio(Peticio p) {
        peticions.add(p);
        esdeveniments.add(new ArribadaPeticio(p.obtenirHoraMinimaRecollida(), p));

    }

    /**
     * @pre cert
     * @post Es genera una petició de trasllat amb dades aleatòries i s'afegeix
     * a la llista de peticions.
     */
    public void afegirPeticioAleatoria() {

    }

    /**
     * @pre cert
     * @post Inicia l'execució de la simulació.
     *
     */
    public void iniciar() {
        while (!esdeveniments.isEmpty() && horaActual.before(horaFi)) {
            Event e = esdeveniments.poll();
            horaActual = e.getTemps();
            e.executar(this);
        }

        finalitzar();
    }

    /**
     * @pre !peticions.isEmpty()
     * @post S'elimina la petició ja servida
     *
     */
    private Peticio peticioActual() {
        return peticions.get(0);

    }

    /**
     * @pre !peticions.isEmpty()
     * @post S'elimina la petició ja servida
     *
     */
    private void peticioServida() {
        peticions.remove(0);

    }

    /**
     * @pre cert
     * @post Tanca la simulació i mostra un resum dels resultats.
     */
    private void finalitzar() {

    }

    /**
     * @brief Retorna el nombre de peticions no servides.
     *
     * @pre Cert.
     * @post Retorna el nombre de peticions que no han pogut ser servides.
     *
     * @return Nombre de peticions no servides.
     */
    public int obtenirPeticionsNoServides() {
        return this.peticions.size();
    }

    ;

    /**
     * @pre Cert.
     * @post Retorna el temps mig d'espera en minuts.
     * 
     * @return Temps mitjà d'espera en minuts.
     */
    public double calcularTempsEsperaMitja() {
        if (peticions.isEmpty()) {
            return 0.0;
        }

        double sumaTemps = 0.0;

        for (Peticio p : peticions) {
            sumaTemps += p.diferenciaEnMinuts();
        }

        return sumaTemps / peticions.size();}

    };
