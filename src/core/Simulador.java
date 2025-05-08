package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.Timer;

import events.Event;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import views.MapPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @class Simulador
 * @brief Classe que gestiona l'execució de la simulació.
 * @details Controla els vehicles, conductors i peticions, processant-les en
 *          funció del temps.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Simulador {

    private List<Vehicle> vehicles; /// < Vehicles disponibles per la simulació.
    private List<Conductor> conductors; /// < Conductors disponibles per la simulació.
    private List<Peticio> peticions; /// < Peticions pendents de servei.
    private LocalTime horaInici; /// < Hora d'inici de la simulació.
    private LocalTime horaFi; /// < Hora de finalització de la simulació.
    private LocalTime horaActual; /// < Hora actual de la simulació.
    private Mapa mapa; /// < Mapa de la ciutat.
    private PriorityQueue<Event> esdeveniments = new PriorityQueue<>(); /// < Esdeveniments programats.
    private MapPanel mapPanel; /// < Panell per mostrar el mapa i els vehicles.

    public Simulador(LocalTime horaInici, LocalTime horaFi, Mapa mapa, List<Vehicle> vehicles,
            List<Conductor> conductors, List<Peticio> peticions) {
        this.vehicles = vehicles;
        this.conductors = conductors;
        this.peticions = peticions;
        this.horaInici = horaInici;
        this.horaFi = horaFi;
        this.mapa = mapa;
        this.horaActual = horaInici;
        esdeveniments = new PriorityQueue<>();
        assignarPeticions();
    }

    public void assignarPeticions() {
        List<Peticio> peticionsAssignades = new ArrayList<>();

        for (Peticio peticio : peticions) {
            if (peticio.estatActual() == EstatPeticio.PENDENT) {
                Lloc origenPeticio = peticio.obtenirOrigen();
                Lloc destiPeticio = peticio.obtenirDesti();

                Conductor millorConductor = null;
                double millorDistancia = Double.MAX_VALUE;
                double millorTemps = Double.MAX_VALUE;

                for (Conductor conductor : conductors) {
                    Vehicle vehicle = conductor.getVehicle();
                    Lloc ubicacio = vehicle.getUbicacioActual();

                    List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacio, origenPeticio);
                    List<Lloc> camiFinsDesti = mapa.camiVoraç(origenPeticio, destiPeticio);
                    List<Lloc> camiTotal = mapa.camiVoraç(ubicacio, destiPeticio);

                    if (camiFinsOrigen != null && camiFinsDesti != null && camiTotal != null) {
                        double distanciaFinsOrigen = mapa.calcularDistanciaRuta(camiFinsOrigen);
                        double distanciaFinsDesti = mapa.calcularDistanciaRuta(camiFinsDesti);

                        double tempsFinsOrigen = mapa.calcularTempsRuta(camiFinsOrigen);

                        double tempsFinsDesti = mapa.calcularTempsRuta(camiFinsDesti);

                        LocalTime horaArribadaPrevista = horaActual
                                .plusMinutes((long) (tempsFinsOrigen + tempsFinsDesti));

                        if (horaArribadaPrevista.isBefore(peticio.obtenirHoraMaximaArribada())) {
                            if (!conductor.isOcupat()) {
                                if (conductorPotServirPeticio(ubicacio, origenPeticio, destiPeticio,
                                        conductor, distanciaFinsOrigen + distanciaFinsDesti)) {
                                    // Si el vehicle pot fer la petició, buscar el millor conductor
                                    if (distanciaFinsOrigen < millorDistancia) {
                                        millorDistancia = distanciaFinsOrigen;
                                        millorConductor = conductor;
                                        millorTemps = tempsFinsOrigen;
                                    }
                                } else {
                                    // Si el vehicle no pot fer la petició, buscar un parquing
                                    System.out.println("El conductor esta ocupat");
                                    Ruta r = conductor.planificarCarrega(mapa, horaInici);
                                    afegirEsdeveniment(new IniciRutaEvent(horaActual, conductor,
                                            conductor.getVehicle(), r));
                                }
                            }
                        }

                    }
                }

                if (millorConductor != null) {
                    // Si el vehicle no es troba a l'origen de la petició, moure'l
                    if (millorConductor.getVehicle().getUbicacioActual() != origenPeticio) {
                        LocalTime horaSortida = peticio.obtenirHoraMinimaRecollida()
                                .minusMinutes((long) millorTemps);
                        afegirEsdeveniment(new MoureVehicleEvent(horaSortida, millorConductor.getVehicle(),
                                millorConductor.getVehicle().getUbicacioActual(), origenPeticio, millorDistancia));
                    }

                    Ruta ruta = millorConductor.planificarRuta(peticio, mapa);
                    if (ruta != null) {
                        peticio.peticioEnProces();
                        LocalTime horaIniciRuta = ruta.obtenirHoraInici();
                        if (!horaIniciRuta.isBefore(horaActual)) {
                            afegirEsdeveniment(new IniciRutaEvent(horaIniciRuta, millorConductor,
                                    millorConductor.getVehicle(), ruta));
                            millorConductor.setOcupat(true);
                            peticionsAssignades.add(peticio);
                        } else
                            System.out
                                    .println("No hi ha temps per fer la ruta de la petició " + peticio.obtenirId());
                    }

                } else
                    System.out.println("Cap conductor pot arribar a la petició " + peticio.obtenirId());
            }
        }

        peticions.removeAll(peticionsAssignades);
    }

    /**
     * @pre ubicacioActual != null, origenPeticio != null, destiPeticio != null,
     * @param ubicacioActual
     * @param origenPeticio
     * @param destiPeticio
     * @param conductor
     * @return true si el conductor pot servir la petició, false en cas contrari.
     */
    private boolean conductorPotServirPeticio(Lloc ubicacioActual, Lloc origenPeticio, Lloc destiPeticio,
            Conductor conductor, double distancia) {
        return (ubicacioActual.obtenirId() == origenPeticio.obtenirId()
                || conductor.teBateria(distancia))
                && conductor.teBateria(distancia);
    }

    /**
     * @pre cert
     * @post Inicia l'execució de la simulació.
     *
     */
    public void iniciar() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!esdeveniments.isEmpty() && horaActual.isBefore(horaFi)) {
                    Event event = esdeveniments.poll();
                    horaActual = event.getTemps();
                    mapPanel.setHoraActual(horaActual);
                    System.out.println("-----------------");

                    System.out.println("Hora actual: " + horaActual);
                    event.executar(Simulador.this);
                } else {
                    ((Timer) e.getSource()).stop();
                    finalitzar();
                }
            }
        });
        timer.start();

    }

    /**
     * @pre cert
     * @post Es genera una petició de trasllat amb dades aleatòries i s'afegeix
     *       a la llista de peticions.
     */
    public void afegirPeticioAleatoria(List<Lloc> llocsDisponibles) {
        Random random = new Random();

        if (llocsDisponibles.size() < 2)
            return;

        // Triar origen i destí diferents
        Lloc desti, origen;
        boolean hiHaCami = true;
        do {
            hiHaCami = true;
            origen = llocsDisponibles.get(random.nextInt(llocsDisponibles.size()));
            desti = llocsDisponibles.get(random.nextInt(llocsDisponibles.size()));
            if (desti.equals(origen) || mapa.hihaCami(origen, desti) == null)
                hiHaCami = false;
        } while (!hiHaCami);

        // Generar hores aleatòries dins el rang de simulació (entre horaInici i horaFi)
        int minutsInici = horaInici.toSecondOfDay() / 60;
        int minutsFi = horaFi.toSecondOfDay() / 60;
        int marge = minutsFi - minutsInici;

        int minutsRecollida = minutsInici + random.nextInt(marge - 30); // mínim 30 min abans del final
        int minutsArribada = minutsRecollida + 15 + random.nextInt(45); // entre 15 i 60 minuts després

        LocalTime horaMinRecollida = LocalTime.ofSecondOfDay(minutsRecollida * 60);
        LocalTime horaMaxArribada = LocalTime.ofSecondOfDay(minutsArribada * 60);

        int numPassatgers = 1 + random.nextInt(4); // entre 1 i 4 passatgers
        boolean compartida = random.nextBoolean();

        Peticio peticio = new Peticio(99, origen, desti, horaMinRecollida, horaMaxArribada, numPassatgers, compartida);

        // Registrar la petició (pots tenir una llista de peticions al simulador)
        this.peticions.add(peticio);
        System.out.println("Afegida petició aleatòria: " + peticio.obtenirOrigen().obtenirId() + " -> "
                + peticio.obtenirDesti().obtenirId()
                + " (hora mínima recollida: " + peticio.obtenirHoraMinimaRecollida() + ", hora màxima arribada: "
                + peticio.obtenirHoraMaximaArribada() + ", passatgers: " + peticio.obtenirNumPassatgers() + ")");
    }

    /**
     * @pre v != null
     * @post El vehicle s'afegeix a la llista de vehicles disponibles per la
     *       simulació.
     *
     * @param v Vehicle a afegir.
     */
    public void afegirVehicle(Vehicle v) {
        vehicles.add(v);
    }

    /**
     * @pre c != null
     * @post El conductor s'afegeix a la llista de conductors disponibles per la
     *       simulació.
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
        System.out.println("Simulació finalitzada.");
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

        return sumaTemps / peticions.size();
    }

    /**
     * @pre Cert.
     * @post Retorna el mapa de la simulació.
     */
    public Mapa getMapa() {
        return mapa;
    }

    /**
     * @pre Cert.
     * @post Fa un set de la vista del mapa.
     */
    public void setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    /**
     * @pre Cert.
     * @post Retorna el panell del mapa.
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }

};
