package core;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import static javax.swing.BorderFactory.createEmptyBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import events.CarregarBateriaEvent;
import events.Event;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import views.MapPanel;

/**
 * @class Simulador
 * @brief Classe que gestiona l'execució de la simulació.
 * @details Controla els vehicles, conductors i peticions, processant-les en
 * funció del temps.
 *
 * @author Dídac Gros Labrador
 * @version 2025.03.04
 */
public class Simulador {

    private List<Vehicle> vehicles;
    /// < Vehicles disponibles per la simulació.
    private List<Conductor> conductors;
    /// < Conductors disponibles per la simulació.
    private List<Peticio> peticions;
    /// < Peticions pendents de servei.
    private LocalTime horaInici;
    /// < Hora d'inici de la simulació.
    private LocalTime horaFi;
    /// < Hora de finalització de la simulació.
    private LocalTime horaActual;
    /// < Hora actual de la simulació.
    private Mapa mapa;
    /// < Mapa de la ciutat.
    private PriorityQueue<Event> esdeveniments = new PriorityQueue<>();
    /// < Esdeveniments programats.
    private MapPanel mapPanel;
    /// < Panell per mostrar el mapa i els vehicles.
    private Estadistiques estadistiques = new Estadistiques();

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

    public Simulador(File JsonFile, List<Vehicle> vehiclesSimulacio) {
        // CONSTRUCTOR PER L OPTIMITZADOR
        List<Lloc> llocs = LectorJSON.carregarLlocs(JsonFile.getAbsolutePath());
        Map<Integer, Lloc> llocs_ID = LectorJSON.convertirLlistaAMap_Llocs(llocs);
        List<Cami> camins = LectorJSON.carregarCamins(JsonFile.getAbsolutePath(), llocs_ID);
        if (vehiclesSimulacio == null) {
            this.vehicles = LectorJSON.carregarVehicles(JsonFile.getAbsolutePath(), llocs_ID);
        } else {
            this.vehicles = vehiclesSimulacio;

        }
        this.conductors = LectorJSON.carregarConductors(JsonFile.getAbsolutePath(),
                LectorJSON.convertirLlistaAMap_Vehicles(vehicles), llocs_ID);
        this.peticions = LectorJSON.carregarPeticions(JsonFile.getAbsolutePath(), llocs_ID);
        this.horaInici = LectorJSON.carregarHorari(JsonFile.getAbsolutePath())[0];
        this.horaFi = LectorJSON.carregarHorari(JsonFile.getAbsolutePath())[1];
        Mapa mapa_Nou = new Mapa();

        for (Lloc lloc : llocs) {
            mapa_Nou.afegirLloc(lloc);
        }

        for (Cami cami : camins) {
            mapa_Nou.afegirCami(cami);
        }

        this.mapa = mapa_Nou;
        this.horaActual = horaInici;
        esdeveniments = new PriorityQueue<>();
    }

    /**
     * @brief Assigna peticions pendents als conductors de tipus ConductorVorac
     * utilitzant una estratègia voraça.
     *
     * Aquesta funció recorre totes les peticions pendents i intenta
     * assignar-les al conductor més proper (en distància fins a l'origen de la
     * petició) que pugui atendre la petició (capacitat i bateria suficient), i
     * que pugui arribar abans de l'hora límit d'arribada.
     *
     * @pre El conjunt de peticions i conductors ha d’estar inicialitzat.
     * @post Algunes peticions poden ser assignades a conductors i es generen
     * esdeveniments de moviment i inici de ruta.
     * @post S'actualitzen les estadístiques de peticions servides i no
     * servides.
     */
    public void assignarPeticionsvorac() {
        List<Peticio> peticionsAssignades = new ArrayList<>();

        for (Peticio peticio : peticions) {
            if (peticio.estatActual() == EstatPeticio.PENDENT) {
                Lloc origenPeticio = peticio.obtenirOrigen();
                Lloc destiPeticio = peticio.obtenirDesti();

                ConductorVorac millorConductor = null;
                double millorDistancia = Double.MAX_VALUE;
                double millorTemps = Double.MAX_VALUE;

                for (Conductor conductor : conductors) {
                    if (conductor instanceof ConductorVorac) {
                        Vehicle vehicle = conductor.getVehicle();
                        Lloc ubicacio = vehicle.getUbicacioActual();

                        // Calcular camins per avaluar si pot fer la petició
                        List<Lloc> camiFinsOrigen = mapa.camivorac(ubicacio, origenPeticio);
                        List<Lloc> camiFinsDesti = mapa.camivorac(origenPeticio, destiPeticio);
                        List<Lloc> camiTotal = mapa.camivorac(ubicacio, destiPeticio);

                        if (camiFinsOrigen != null && camiFinsDesti != null && camiTotal != null) {
                            double distanciaFinsOrigen = mapa.calcularDistanciaRuta(camiFinsOrigen);
                            double distanciaFinsDesti = mapa.calcularDistanciaRuta(camiFinsDesti);
                            double distanciaTotal = distanciaFinsOrigen + distanciaFinsDesti;

                            double tempsFinsOrigen = mapa.calcularTempsRuta(camiFinsOrigen);
                            double tempsFinsDesti = mapa.calcularTempsRuta(camiFinsDesti);

                            // Calcular hora d’arribada prevista al destí
                            LocalTime horaArribadaPrevista = horaActual
                                    .plusMinutes((long) (tempsFinsOrigen + tempsFinsDesti));

                            if (horaArribadaPrevista.isBefore(peticio.obtenirHoraMaximaArribada())) {
                                // Verificar capacitat i bateria
                                if (conductor.potServirPeticio(peticio.obtenirNumPassatgers())) {
                                    if (conductor.teBateria(distanciaTotal, this, mapa, horaInici, horaActual)) {
                                        // Guardar el millor conductor (més proper a l'origen)
                                        if (distanciaFinsOrigen < millorDistancia) {
                                            millorDistancia = distanciaFinsOrigen;
                                            millorConductor = (ConductorVorac) conductor;
                                            millorTemps = tempsFinsOrigen;

                                            // Estadístiques
                                            this.estadistiques.registrarOcupacionVehiculo(vehicle.getPassatgersActuals());
                                            this.estadistiques.registrarPeticionServida(tempsFinsOrigen);
                                        }
                                    } else {
                                        System.out.println("\nEl vehicle no pot fer la petició, ja que no té bateria.");
                                    }
                                } else {
                                    System.out.println("El vehicle no pot fer la petició.");
                                }
                            }
                        } else {
                            System.out.println(
                                    "El vehicle no pot fer la petició, ja que no hi ha camí entre vehicle i petició.");
                        }
                    }
                }

                if (millorConductor != null) {
                    // Si el vehicle no està a l'origen, generar esdeveniment de moviment previ
                    if (millorConductor.getVehicle().getUbicacioActual() != origenPeticio) {
                        LocalTime horaSortida = peticio.obtenirHoraMinimaRecollida()
                                .minusMinutes((long) millorTemps);
                        afegirEsdeveniment(new MoureVehicleEvent(horaActual, millorConductor.getVehicle(),
                                millorConductor.getVehicle().getUbicacioActual(), origenPeticio, millorDistancia));
                        //PENDENT CALCULAR TEMPS FINS AL PUNT ON ES DEMANA LA PETICIO
                        //this.estadistiques.registrarPeticionServida();

                    }

                    // Planificar i assignar la ruta
                    Ruta ruta = millorConductor.planificarRuta(peticio, mapa);
                    if (ruta != null) {
                        peticio.peticioEnProces();
                        LocalTime horaIniciRuta = ruta.obtenirHoraInici();
                        if (!horaIniciRuta.isBefore(horaActual)) {
                            afegirEsdeveniment(new IniciRutaEvent(horaIniciRuta, millorConductor,
                                    millorConductor.getVehicle(), ruta));
                            millorConductor.setOcupat(true);
                            peticionsAssignades.add(peticio);
                            this.estadistiques.registrarEstadoBateria(millorConductor.getVehicle().obtenirBateria());
                            this.estadistiques.registrarPeticionServida(calcularTempsEsperaMitja());
                            this.estadistiques.registrarOcupacionVehiculo(ruta.obtenirPassatgersPeticio());
                            this.estadistiques.registrarTiempoViaje(ruta.obtenirTempsTotal());

                        } else {
                            System.out.println("No hi ha temps per fer la ruta de la petició " + peticio.obtenirId());
                        }
                    }
                } else {
                    System.out.println("Cap conductor pot arribar a la petició " + peticio.obtenirId());
                }
            }
        }

        // Registrar les peticions que no s’han pogut assignar
        estadistiques.registrarPeticionNoServida(peticionsAssignades.size());
        peticions.removeAll(peticionsAssignades);

    }

    /**
     * @brief Assigna rutes planificades als conductors de tipus
     * ConductorPlanificador.
     *
     * Aquesta funció demana a cada conductor planificador que intenti generar
     * una ruta òptima tenint en compte totes les peticions disponibles. Si la
     * ruta és vàlida, s’afegeix un esdeveniment d’inici de ruta a la simulació.
     *
     * @pre La llista de conductors i peticions ha d’estar inicialitzada i no
     * ser nul·la.
     * @post Alguns conductors poden iniciar una ruta planificada, i es generen
     * esdeveniments corresponents.
     */
    public void assignarPeticionsPlan() {
        for (Conductor conductor : conductors) {
            if (conductor instanceof ConductorPlanificador) {
                ConductorPlanificador conductorPlani = (ConductorPlanificador) conductor;

                // Es demana al conductor que planifiqui una ruta segons les peticions actuals
                Ruta r = conductorPlani.planificarRuta(peticions, this, horaActual);

                if (r != null) {
                    this.estadistiques.registrarEstadoBateria(conductorPlani.getVehicle().obtenirBateria());
                    this.estadistiques.registrarPeticionServida(calcularTempsEsperaMitja());
                    this.estadistiques.registrarOcupacionVehiculo(r.obtenirPassatgersPeticio());
                    this.estadistiques.registrarTiempoViaje(r.obtenirTempsTotal());

                    afegirEsdeveniment(new IniciRutaEvent(
                            r.getHoraInici(),
                            conductorPlani,
                            conductorPlani.getVehicle(),
                            r));
                } else {
                    // Si no s’ha pogut planificar ruta, informar per consola
                    System.out.println("No hi ha ruta per al conductor planificador " + conductorPlani.getId());
                }
            }
        }
    }

    /**
     * @pre cert
     * @post Inicia l'execució de la simulació.
     *
     */
    public void iniciar(File jsonFile) {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!esdeveniments.isEmpty() && horaActual.isBefore(horaFi)) {
                    Event event = esdeveniments.poll();
                    horaActual = event.getTemps();
                    mapPanel.setHoraActual(horaActual);

                    event.executar(Simulador.this);

                } // else if (esdeveniments.isEmpty() && !peticions.isEmpty()) {
                // // Si no hi ha esdeveniments però hi ha peticions, reintenta
                // assignarPeticions();
                // }
                else {
                    finalitzarSimulacio(e, jsonFile, true);

                }
            }
        });
        timer.start();
    }

    /**
     * @pre cert
     * @post Es genera una petició de trasllat amb dades aleatòries i s'afegeix
     * a la llista de peticions.
     */
    public void afegirPeticioAleatoria(List<Lloc> llocsDisponibles) {
        Random random = new Random();

        if (llocsDisponibles.size() < 2) {
            return;
        }

        // Triar origen i destí diferents
        Lloc desti, origen;
        boolean hiHaCami = true;
        do {
            hiHaCami = true;
            origen = llocsDisponibles.get(random.nextInt(llocsDisponibles.size()));
            desti = llocsDisponibles.get(random.nextInt(llocsDisponibles.size()));
            if (desti.equals(origen) || mapa.hihaCami(origen, desti) == null) {
                hiHaCami = false;
            }
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
        int idRandom = 1 + random.nextInt(99); // entre 1 i 4 passatgers

        Peticio peticio = new Peticio(idRandom, origen, desti, horaMinRecollida, horaMaxArribada, numPassatgers, compartida);
        // Registrar la petició (pots tenir una llista de peticions al simulador)
        this.peticions.add(peticio);

        String missatge = "Afegida petició aleatòria: " + peticio.obtenirOrigen().obtenirId() + " -> "
                + peticio.obtenirDesti().obtenirId()
                + " ( recollida: " + peticio.obtenirHoraMinimaRecollida() + ", arribada: "
                + peticio.obtenirHoraMaximaArribada() + ")";
        System.out.println(missatge);
        pintarMissatge(missatge);
        assignarPeticions();
    }

    /**
     * @pre cert
     * @post Assigna les peticions pendents als conductors disponibles.
     */
    public void assignarPeticions() {
        assignarPeticionsPlan();
        assignarPeticionsvorac();
    }

    /**
     * @pre cert
     * @post Inicia l'execució d'una simulacio guardada
     *
     */
    public void executarSimulacioGuardada(File jsonFile) {

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!esdeveniments.isEmpty() && horaActual.isBefore(horaFi)) {
                    Event event = esdeveniments.poll();
                    horaActual = event.getTemps();
                    mapPanel.setHoraActual(horaActual);

                    event.executar(Simulador.this);

                } else {
                    finalitzarSimulacio(e, jsonFile, false);

                }
            }
        });
        timer.start();
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
    }

    /**
     * @pre peticions != null
     * @post Comprova si queden peticions pendents.
     * @return True si hi ha peticions pendents, false en cas contrari.
     */
    public boolean hiHaPeticions() {
        return !peticions.isEmpty();
    }

    /**
     * @param e
     * @pre cert
     * @post Tanca la simulació i mostra un resum dels resultats.
     */
    private void finalitzarSimulacio(ActionEvent e, File jsonFile, boolean guardarDades) {
        try {
            ((Timer) e.getSource()).stop();

            System.out.println("------------------");
            System.out.println("Estadistiques:");
            System.out.println(this.estadistiques.toString());

            mostrarDialogEstadistiques(!horaActual.isBefore(horaFi));
            LectorJSON escritorJSON = new LectorJSON();

            List<Lloc> listDeLlocs = new ArrayList<>(mapa.getLlocs().keySet());
            List<Cami> listCami = mapa.obtenirTotsElsCamins();

            if (guardarDades) {
                if (jsonFile != null) {
                    escritorJSON.writeJsonFile(this.conductors, this.vehicles, listDeLlocs, listCami, this.peticions, this.estadistiques, this.esdeveniments, jsonFile.getAbsolutePath());
                }
            }
            System.out.println("Simulació finalitzada.");
        } catch (IOException ex) {
            System.err.println("ERROR AL FINALITZAR SIMULACIO");
        }
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

    public void pintarMissatge(String missatge) {
        mapPanel.afegirMissatge(missatge);
    }

    /**
     * Mostra un diàleg amb les estadístiques de la simulació.
     */
    private void mostrarDialogEstadistiques(boolean finalitzacioPertemps) {
        // Crear un JDialog modal
        JDialog dialog;
        if (finalitzacioPertemps) {
            dialog = new JDialog((Frame) null, "Estadístiques de la Simulació - " + " Simulacio Finalitzada s'ha arribat a la hora final", true);

        } else {
            dialog = new JDialog((Frame) null, "Estadístiques de la Simulació - " + " Simulacio Finalitzada no hi han més peticions per servir", true);

        }
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null); // Centrar en pantalla

        // Crear panel principal con borde y margen
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createEmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("Resum de la Simulació", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titulo, BorderLayout.NORTH);

        // Panel de estadísticas
        JTextArea areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaEstadisticas.setText(obtenerTextoEstadisticas());

        // Añadir scroll por si hay muchas estadísticas
        JScrollPane scrollPane = new JScrollPane(areaEstadisticas);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botón de cierre
        JButton cerrarBtn = new JButton("Tancar");
        cerrarBtn.addActionListener(e -> dialog.dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.add(cerrarBtn);
        panel.add(panelBoton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Genera el texto formateado con las estadísticas.
     *
     * @return String con las estadísticas formateadas
     */
    private String obtenerTextoEstadisticas() {
        StringBuilder sb = new StringBuilder();

        // Aquí añades toda la información de las estadísticas
        sb.append("--- Peticions ---\n");
        sb.append("Nombre de peticions servides -> ").append(this.estadistiques.getPeticionesServidas()).append("\n");
        sb.append("Nombre de peticions no servides -> ").append(this.estadistiques.getPeticionesNoServidas()).append("\n");
        sb.append("Percentatge d'exit de peticions -> ").append(this.estadistiques.getPorcentajeExito()).append("\n");

        sb.append("--- Temps mig ---\n");
        sb.append("Temps mig d'espera -> ").append(this.estadistiques.getTiempoEsperaPromedio()).append("\n\n");
        sb.append("Temps maxim d'espera -> ").append(this.estadistiques.getTiempoMaximoEspera()).append("\n\n");

        sb.append("--- Vehicles ---\n");
        sb.append(String.format("Mitjana del percentatge d’ocupació dels vehicles -> ", this.estadistiques.getOcupacionPromedioVehiculos()) + "\n\n");
        sb.append(String.format("Mitjana del temps dels viatges -> ", this.estadistiques.getTiempoViajePromedio()));
        sb.append(String.format("Bateria Promig -> ", this.estadistiques.getPorcentajeBateriaPromedio()));

        sb.append("\n");

        return sb.toString();
    }

    public boolean peticionsServides() {
        return peticions.isEmpty();
    }

    public void setEstadistiques(Estadistiques estadistiques) {
        this.estadistiques = estadistiques;
    }

    public void setEsdeviments(List<Event> events) {
        // Crear una PriorityQueue con un comparador que ordene por tiempo
        PriorityQueue<Event> eventsQueue = new PriorityQueue<>(
                Comparator.comparing(Event::getTemps)
        );

        // Añadir todos los eventos de la lista a la cola
        eventsQueue.addAll(events);
        esdeveniments = eventsQueue;
    }

    public Map<Integer, Integer> iniciarOptimitzacioPuntsCarrega(File JsonFile) {
        System.out.println("DUNSSS0");

        Map<Integer, Integer> vegadesUsat = new HashMap<Integer, Integer>();
        Map<Lloc, List<Cami>> llocsMapa = mapa.getLlocs();
        List<Lloc> llistaLlocs = new ArrayList<>(llocsMapa.keySet());
        for (int i = 0; i < llistaLlocs.size(); i++) {
            System.out.println("DUNSSS2");
            if (llistaLlocs.get(i) instanceof Parquing) {
                System.out.println("DUNSSS");

                Parquing parquingExistent = (Parquing) llistaLlocs.get(i);

                if (parquingExistent.obtenirPuntsCarregaPublics() > 1 || parquingExistent.obtenirPuntsCarregaPrivats() > 1) {
                    vegadesUsat.put(parquingExistent.obtenirId(), 0);
                    System.out.println("DUNSSS");
                }

            }
        }

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!esdeveniments.isEmpty() && horaActual.isBefore(horaFi)) {
                    Event event = esdeveniments.poll();
                    horaActual = event.getTemps();
                    mapPanel.setHoraActual(horaActual);
                    if (event instanceof CarregarBateriaEvent) {
                        // Fem un casting a CarregaBateria
                        CarregarBateriaEvent carregaEvent = (CarregarBateriaEvent) event;
                        event.executar(Simulador.this);
                        Integer idCarregaador = carregaEvent.getConductor().getVehicle().getUbicacioActual().obtenirId();
                        vegadesUsat.merge(idCarregaador, 1, Integer::sum);
                    } // else if (esdeveniments.isEmpty() && !peticions.isEmpty()) {
                    // // Si no hi ha esdeveniments però hi ha peticions, reintenta
                    // assignarPeticions();
                    // }
                    else {
                        finalitzarSimulacio(e, JsonFile, true);

                    }
                }
            }

        });
        timer.start();

        return vegadesUsat;
    }

};
