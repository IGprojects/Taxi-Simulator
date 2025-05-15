import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Cami;
import core.Conductor;
import core.LectorCSV;
import core.LectorJSON;
import core.Lloc;
import core.Mapa;
import core.Optimitzador;
import core.Peticio;
import core.Simulador;
import core.Vehicle;
import events.Event;
import views.LegendPanel;
import views.MapPanel;
import views.SelectorInicial;
import views.VehiclesComparisonPanel;

/**
 * @class Main
 * @brief Classe principal que inicialitza la simulació
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.13
 */
public class Main {
    /**
     * @pre llocs != null && camins != null
     * @post Afegim tots els llocs i camins al mapa
     * @return Retorna el mapa carregat
     */
    public static Mapa carregarMapa(List<Lloc> llocs, List<Cami> camins) {
        Mapa mapa = new Mapa();

        for (Lloc lloc : llocs) {
            mapa.afegirLloc(lloc);
        }

        for (Cami cami : camins) {
            mapa.afegirCami(cami);
        }

        System.out.println("Mapa carregat correctament!");

        return mapa;
    }

    /**
     * @pre cert
     * @post Inicialitza la simulació amb els fitxers especificats
     * @param llocsFile
     * @param connexionsFile
     * @param vehiclesFile
     * @param conductorsFile
     * @param peticionsFile
     * @param horaInici
     * @param horaFinal
     */
    public static void inicialitzar(File llocsFile, File connexionsFile, File vehiclesFile, File conductorsFile,
            File peticionsFile, File jsonFile, LocalTime horaInici, LocalTime horaFinal) {
        List<Lloc> llocs = LectorCSV.carregarLlocs(llocsFile.getAbsolutePath());
        Map<Integer, Lloc> llocsPerId = new HashMap<>();
        for (Lloc l : llocs)
            llocsPerId.put(l.obtenirId(), l);

        List<Cami> camins = LectorCSV.carregarCamins(connexionsFile.getAbsolutePath(), llocsPerId);
        Mapa mapa = carregarMapa(llocs, camins);

        List<Vehicle> vehicles = LectorCSV.carregarVehicles(vehiclesFile.getAbsolutePath(),
                llocsPerId);
        Map<Integer, Vehicle> vehiclesPerId = new HashMap<>();
        for (Vehicle v : vehicles)
            vehiclesPerId.put(v.getId(), v);

        List<Conductor> conductors = LectorCSV.carregarConductors(conductorsFile.getAbsolutePath(),
                vehiclesPerId, llocsPerId);
        List<Peticio> peticions = LectorCSV.carregarPeticions(peticionsFile.getAbsolutePath(),
                llocsPerId);

        System.out.println("Dades carregades correctament.");
        System.out.println("Hora d'inici: " + horaInici);
        System.out.println("Hora final: " + horaFinal);
        System.out.println("--------------------------------");
        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors,
                peticions);

        mostrarMapa(mapa, simulador, vehicles, llocs, true, null, jsonFile);
    }

    /**
     * @pre cert
     * @post Inicialitza la simulació a partir d'una simulació guardada
     */
    public static void inicialitzarSimulacioGuardada(File SimulacioFile) {
        List<Lloc> llocs = LectorJSON.carregarLlocs(SimulacioFile.getAbsolutePath());
        System.out.println(llocs.size()+"--------------------------------------------------");

        Map<Integer, Lloc> llocsPerId = new HashMap<>();
        for (Lloc l : llocs)
            llocsPerId.put(l.obtenirId(), l);

        List<Cami> camins = LectorJSON.carregarCamins(SimulacioFile.getAbsolutePath(), llocsPerId);
        Mapa mapa = carregarMapa(llocs, camins);
        System.out.println(camins.size()+"--------------------------------------------------");
        List<Vehicle> vehicles = LectorJSON.carregarVehicles(SimulacioFile.getAbsolutePath(),
                llocsPerId);
        Map<Integer, Vehicle> vehiclesPerId = new HashMap<>();
        for (Vehicle v : vehicles)
            vehiclesPerId.put(v.getId(), v);

        List<Conductor> conductors = LectorJSON.carregarConductors(SimulacioFile.getAbsolutePath(), vehiclesPerId,
                llocsPerId);
        LocalTime[] hores = LectorJSON.carregarHorari(SimulacioFile.getAbsolutePath());

        LocalTime horaInici = hores[0];
        LocalTime horaFinal = hores[1];

        List<Event> eventsProgramats = LectorJSON.carregarEvents(SimulacioFile.getAbsolutePath(), vehiclesPerId,
                LectorJSON.convertirLlistaAMap_Conductors(conductors), llocsPerId);

        System.out.println("Dades carregades correctament.");
        System.out.println("Hora d'inici: " + horaInici);
        System.out.println("Hora final: " + horaFinal);
        System.out.println("CAMINS: " + camins.size());
        System.out.println("CONDUCTORS: " + conductors.size());
        System.out.println("VEHICLES: " + vehicles.size());
        System.out.println("--------------------------------");

        // CONSTRUCTOR PER SIMULADOR PER AFEGIR DES DE EL INICI TOTS ELS EVENTS QUE HA
        // DE FER
        List<Peticio> peticions = new ArrayList<Peticio>();
        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors, peticions);
        mostrarMapa(mapa, simulador, vehicles, llocs, false, eventsProgramats, SimulacioFile);
    }

    /**
     * @pre cert
     * @post Inicia el programa
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectorInicial.mostrar(new SelectorInicial.DadesIniciListener() {
                @Override
                public void onDadesCompletades(File llocsFile, File connexionsFile, File vehiclesFile,
                        File conductorsFile, File peticionsFile, File JsonFile,
                        LocalTime horaInici, LocalTime horaFinal) {
                    try {
                        inicialitzar(llocsFile, connexionsFile, vehiclesFile, conductorsFile, peticionsFile, JsonFile,
                                horaInici, horaFinal);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error carregant fitxers: " + e.getMessage());
                    }
                }

                @Override
                public void onSimulacioJsonSeleccionat(File simulacioJson) {
                    try {
                        inicialitzarSimulacioGuardada(simulacioJson);

                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error carregant simulació: " + e.getMessage());
                    }
                }

                 @Override
                public void onOptimitzarSimulacio(File simulacioJson) {

                    Optimitzador optimitzador = new Optimitzador();
                    List<Vehicle> vehiclesTotals = LectorJSON.carregarVehicles(simulacioJson.getAbsolutePath(), LectorJSON.convertirLlistaAMap_Llocs(LectorJSON.carregarLlocs(simulacioJson.getAbsolutePath())));
                    List<Vehicle> vehiclesRedundants = optimitzador.obtenirVehiclesRedundants(simulacioJson,vehiclesTotals);
                    VehiclesComparisonPanel.mostrarComparacio(vehiclesTotals, vehiclesRedundants);
                }
            });
        });
    }

    /**
     * @pre cert
     * @post Mostra el mapa amb la llegenda i el botó d'inici
     * @param mapa
     * @param simulador
     * @param vehicles
     * @param llocs
     */
    private static void mostrarMapa(Mapa mapa, Simulador simulador, List<Vehicle> vehicles, List<Lloc> llocs,
            boolean simulacioReal, List<Event> eventsSimulacioGuardada, File jsonFile) {
        JFrame frame = new JFrame("Visualització del Mapa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        MapPanel mapPanel = new MapPanel(mapa);
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(mapPanel, java.awt.BorderLayout.CENTER);

        // Crea el panell inferior que contindrà la llegenda i el botó
        JPanel panellInferior = new JPanel();
        panellInferior.setLayout(new BoxLayout(panellInferior, BoxLayout.Y_AXIS));
        panellInferior.setBackground(Color.CYAN);

        // Llegenda
        LegendPanel legendPanel = new LegendPanel();
        legendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panellInferior.add(legendPanel);

        // Separació opcional
        panellInferior.add(Box.createVerticalStrut(5));

        // Botó de simulació
        JButton startButton = new JButton("Iniciar Simulació");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panellInferior.add(startButton);
        panellInferior.add(Box.createVerticalStrut(10)); // 10 píxels de marge inferior

        // Afegeix aquest panell al sud del frame
        frame.add(panellInferior, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botoReset = new JButton("Afegir petició aleatòria");
        topPanel.add(botoReset);
        frame.add(topPanel, BorderLayout.NORTH);

        // 🔁 Acció del botó
        botoReset.addActionListener(e -> {
            simulador.afegirPeticioAleatoria(llocs);
        });

        simulador.setMapPanel(mapPanel);
        for (Vehicle vehicle : vehicles) {
            mapPanel.assignarColorVehicle(vehicle);
        }
        if (simulacioReal) {
            startButton.addActionListener(e -> {
                simulador.iniciar(jsonFile);
            });
        } else {
            startButton.addActionListener(e -> {
                simulador.executarSimulacioGuardada(eventsSimulacioGuardada, jsonFile);
            });
        }
        frame.setVisible(true);
    }

}
