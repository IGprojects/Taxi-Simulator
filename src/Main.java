import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import core.Peticio;
import core.Simulador;
import core.Vehicle;
import events.Event;
import views.MapPanel;
import views.SelectorInicial;

public class Main {

    public static Mapa carregarMapa(List<Lloc> llocs, List<Cami> camins) {
        Mapa mapa = new Mapa(llocs.size(), camins.size());

        for (Lloc lloc : llocs) {
            mapa.afegirLloc(lloc);
        }

        for (Cami cami : camins) {
            mapa.afegirCami(cami);
        }

        System.out.println("Mapa carregat correctament!");

        return mapa;
    }

    public static void inicialitzar(File llocsFile, File connexionsFile, File vehiclesFile, File conductorsFile,
            File peticionsFile,
            LocalTime horaInici, LocalTime horaFinal) {
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
                vehiclesPerId);
        List<Peticio> peticions = LectorCSV.carregarPeticions(peticionsFile.getAbsolutePath(),
                llocsPerId);

        System.out.println("Dades carregades correctament.");
        System.out.println("Hora d'inici: " + horaInici);
        System.out.println("Hora final: " + horaFinal);
        System.out.println("--------------------------------");
        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors,
                peticions);

        mostrarMapa(mapa, simulador, vehicles, llocs);
    }




    //metode per inicialitzar a partir d'una simulacio guardada obtinguda d un JSON
     public static void inicialitzarSimulacioGuardada(File SimulacioFile) {
        List<Lloc> llocs = LectorJSON.carregarLlocs(SimulacioFile.getAbsolutePath());
        Map<Integer, Lloc> llocsPerId = new HashMap<>();
        for (Lloc l : llocs)
            llocsPerId.put(l.obtenirId(), l);

        List<Cami> camins = LectorJSON.carregarCamins(SimulacioFile.getAbsolutePath(), llocsPerId);
        Mapa mapa = carregarMapa(llocs, camins);

        List<Vehicle> vehicles = LectorJSON.carregarVehicles(SimulacioFile.getAbsolutePath(),
                llocsPerId);
        Map<Integer, Vehicle> vehiclesPerId = new HashMap<>();
        for (Vehicle v : vehicles)
            vehiclesPerId.put(v.getId(), v);

        List<Conductor> conductors = LectorJSON.carregarConductors(SimulacioFile.getAbsolutePath(),vehiclesPerId);
        LocalTime [] hores=LectorJSON.carregarHorari(SimulacioFile.getAbsolutePath());
        LocalTime horaInici=hores[0];
        LocalTime horaFinal=hores[1];

        List<Event> eventsProgramats=LectorJSON.carregarEvents(SimulacioFile.getAbsolutePath(), vehiclesPerId, LectorJSON.convertirLlistaAMap_Conductors(conductors), llocsPerId);

        System.out.println("Dades carregades correctament.");
        System.out.println("Hora d'inici: " + horaInici);
        System.out.println("Hora final: " + horaFinal);
        System.out.println("--------------------------------");


        //CONSTRUCTOR PER SIMULADOR PER AFEGIR DES DE EL INICI TOTS ELS EVENTS QUE HA DE FER
        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors,null);
        mostrarMapa(mapa, simulador, vehicles, llocs);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        SelectorInicial.mostrar(new SelectorInicial.DadesIniciListener() {
            @Override
            public void onDadesCompletades(File llocsFile, File connexionsFile, File vehiclesFile, 
                                         File conductorsFile, File peticionsFile,
                                         LocalTime horaInici, LocalTime horaFinal) {
                try {
                    inicialitzar(llocsFile, connexionsFile, vehiclesFile, conductorsFile, peticionsFile,
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
        });
    });


        // // Execució per terminal (amb fitxers com a arguments)
        // try {
        // String fitxerLlocs = args[0];
        // String fitxerCamins = args[1];
        // String fitxerVehicles = args[2];
        // String fitxerConductors = args[3];
        // String fitxerPeticions = args[4];
        // LocalTime horaInici = LocalTime.parse(args[5]);
        // LocalTime horaFinal = LocalTime.parse(args[6]);

        // List<Lloc> llocs = LectorCSV.carregarLlocs(fitxerLlocs);
        // Map<Integer, Lloc> llocsPerId = new HashMap<>();
        // for (Lloc l : llocs)
        // llocsPerId.put(l.obtenirId(), l);

        // List<Cami> camins = LectorCSV.carregarCamins(fitxerCamins, llocsPerId);
        // Mapa mapa = carregarMapa(llocs, camins);

        // List<Vehicle> vehicles = LectorCSV.carregarVehicles(fitxerVehicles,
        // llocsPerId);
        // Map<Integer, Vehicle> vehiclesPerId = new HashMap<>();
        // for (Vehicle v : vehicles)
        // vehiclesPerId.put(v.getId(), v);

        // List<Conductor> conductors = LectorCSV.carregarConductors(fitxerConductors,
        // vehiclesPerId);
        // List<Peticio> peticions = LectorCSV.carregarPeticions(fitxerPeticions,
        // llocsPerId);

        // System.out.println("Dades carregades correctament.");
        // System.out.println("Hora d'inici: " + horaInici);
        // System.out.println("Hora final: " + horaFinal);

        // Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles,
        // conductors, peticions);
        // simulador.iniciar();

        // } catch (Exception e) {
        // System.err.println("Error executant el programa amb fitxers: " +
        // e.getMessage());
        // e.printStackTrace();
        // System.exit(1);
        // }
    }

    private static void mostrarMapa(Mapa mapa, Simulador simulador, List<Vehicle> vehicles, List<Lloc> llocs) {
        JFrame frame = new JFrame("Visualització del Mapa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        MapPanel mapPanel = new MapPanel(mapa);
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(mapPanel, java.awt.BorderLayout.CENTER);

        JButton startButton = new JButton("Iniciar Simulació");
        frame.add(startButton, java.awt.BorderLayout.SOUTH);

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
        startButton.addActionListener(e -> {
            simulador.iniciar();
        });

        frame.setVisible(true);
    }
}
