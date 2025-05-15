package core;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.Timer;

import events.Event;
import events.FiRutaEvent;
import events.IniciRutaEvent;
import events.MoureVehicleEvent;
import views.MapPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static javax.swing.BorderFactory.createEmptyBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
    private Estadistiques estadistiques=new Estadistiques();

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
                    if(conductor.getId() == 3) {
                        System.out.println("Ocupat: " + conductor.isOcupat());
                    }
                    Vehicle vehicle = conductor.getVehicle();
                    Lloc ubicacio = vehicle.getUbicacioActual();

                    List<Lloc> camiFinsOrigen = mapa.camiVoraç(ubicacio, origenPeticio);
                    List<Lloc> camiFinsDesti = mapa.camiVoraç(origenPeticio, destiPeticio);
                    List<Lloc> camiTotal = mapa.camiVoraç(ubicacio, destiPeticio);

                    if (camiFinsOrigen != null && camiFinsDesti != null && camiTotal != null) {
                        double distanciaFinsOrigen = mapa.calcularDistanciaRuta(camiFinsOrigen);
                        double distanciaFinsDesti = mapa.calcularDistanciaRuta(camiFinsDesti);
                        double distanciaTotal = distanciaFinsDesti + distanciaFinsOrigen;

                        //temps d espera fins arribar al client
                        double tempsFinsOrigen = mapa.calcularTempsRuta(camiFinsOrigen);

                        double tempsFinsDesti = mapa.calcularTempsRuta(camiFinsDesti);

                        LocalTime horaArribadaPrevista = horaActual
                                .plusMinutes((long) (tempsFinsOrigen + tempsFinsDesti));

                        if (horaArribadaPrevista.isBefore(peticio.obtenirHoraMaximaArribada())) {
                            if (conductor.potServirPeticio(peticio.obtenirNumPassatgers())) {
                                System.out.println("Conductoraaaaaaaaaaaaaaaaa" + conductor.getId());
                                
                                if (conductor.teBateria(distanciaTotal, this, mapa, horaInici, horaActual)) {
                                    // Si el vehicle pot fer la petició, buscar el millor conductor
                                    if (distanciaFinsOrigen < millorDistancia) {
                                        millorDistancia = distanciaFinsOrigen;
                                        millorConductor = conductor;
                                        millorTemps = tempsFinsOrigen;
                                        estadistiques.registrarOcupacionVehiculo(vehicle.getPassatgersActuals());
                                        estadistiques.registrarPeticionServida(tempsFinsOrigen);
                                    }
                                } else
                                    System.out.println("\nEl vehicle no pot fer la petició, ja que no té bateria.");
                            } else
                                System.out.println("El vehicle no pot fer la petició.");
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

        //peticions no servides
        estadistiques.registrarPeticionNoServida(peticionsAssignades.size());
        peticions.removeAll(peticionsAssignades);
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
                    System.out.println("Hora actual: " + horaActual);
                  
                    event.executar(Simulador.this);

                } else if (esdeveniments.isEmpty() && !peticions.isEmpty()) {
                    // Si no hi ha esdeveniments però hi ha peticions, reintenta
                    assignarPeticions();

                } else {
                    finalitzarSimulacio(e,jsonFile);
                }
            }
        });
        timer.start();
    }


    /**
     * @pre cert
     * @post Inicia l'execució d'una simulacio guardada
     *
     */
   public void executarSimulacioGuardada(List<Event> EventsGuardats,File jsonFile) {
    Timer timer = new Timer(1000, new ActionListener() {
        private Iterator<Event> eventIterator = EventsGuardats.iterator();
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (eventIterator.hasNext() && horaActual.isBefore(horaFi)) {
                Event event = eventIterator.next();
                horaActual = event.getTemps();
                mapPanel.setHoraActual(horaActual);
                System.out.println("Hora actual: " + horaActual);
              
                event.executar(Simulador.this);
                
                // Si s'han acabat els events guardats, passar a les peticions pendents
                if (!eventIterator.hasNext() && !peticions.isEmpty()) {
                    assignarPeticions();
                }
            } else {
                finalitzarSimulacio(e,jsonFile);
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
     * @param e
     * @pre cert
     * @post Tanca la simulació i mostra un resum dels resultats.
     */
    private void finalitzarSimulacio(ActionEvent e,File jsonFile) {
        try {
            ((Timer) e.getSource()).stop();
            
            System.out.println("------------------");
            System.out.println("Estadistiques:");
            System.out.println(this.estadistiques.toString());
            mostrarDialogEstadistiques();
            LectorJSON escritorJSON=new LectorJSON();
            //mapa.getLlocs().keySet().toArray()
            List<Lloc> listDeLlocs = new ArrayList<>(mapa.getLlocs().keySet());
            
            Collection collectionGenerica = mapa.getLlocs().values();
            List<Cami> listCami = new ArrayList<Cami>(collectionGenerica);
            
            escritorJSON.writeJsonFile(conductors,vehicles,listDeLlocs,listCami,jsonFile.getAbsolutePath());
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


    /**
 * Mostra un diàleg amb les estadístiques de la simulació.
 */
private void mostrarDialogEstadistiques() {
    // Crear un JDialog modal
    JDialog dialog = new JDialog((Frame)null, "Estadístiques de la Simulació", true);
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
 * @return String con las estadísticas formateadas
 */
private String obtenerTextoEstadisticas() {
    StringBuilder sb = new StringBuilder();
    
    // Aquí añades toda la información de las estadísticas
    sb.append("--- Peticions ---\n");
    sb.append("Nombre de peticions servides -> ").append(estadistiques.getPeticionesServidas()).append("\n");
    sb.append("Nombre de peticions no servides -> ").append(estadistiques.getPeticionesNoServidas()).append("\n");
    sb.append("Percentatge d'exit de peticions -> ").append(estadistiques.getPorcentajeExito()).append("\n");

    sb.append("--- Temps mig ---\n");
    sb.append("Temps mig d'espera -> ").append(estadistiques.getTiempoEsperaPromedio()).append("\n\n");
    sb.append("Temps maxim d'espera -> ").append(estadistiques.getTiempoMaximoEspera()).append("\n\n");

    sb.append("--- Vehicles ---\n");
    sb.append(String.format("Mitjana del percentatge d’ocupaci´o dels vehicles -> ", estadistiques.getOcupacionPromedioVehiculos()));
    sb.append(String.format("Mitjana del temps dels viatges -> ", estadistiques.getTiempoViajePromedio()));
    sb.append("\n");
    
    return sb.toString();
}

};
