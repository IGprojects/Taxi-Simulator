import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.Cami;
import core.Conductor;
import core.LectorCSV;
import core.Lloc;
import core.Mapa;
import core.Peticio;
import core.Simulador;
import core.Vehicle;
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

        Simulador simulador = new Simulador(horaInici, horaFinal, mapa, vehicles, conductors,
                peticions);
        mostrarMapa(mapa, simulador);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectorInicial
                    .mostrar((llocsFile, connexionsFile, vehiclesFile, conductorsFile, peticionsFile, horaInici,
                            horaFinal) -> {
                        try {
                            inicialitzar(llocsFile, connexionsFile, vehiclesFile, conductorsFile, peticionsFile,
                                    horaInici, horaFinal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error carregant fitxers: " + e.getMessage());
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

    private static void mostrarMapa(Mapa mapa, Simulador simulador) {
        JFrame frame = new JFrame("Visualització del Mapa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        MapPanel mapPanel = new MapPanel(mapa);
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(mapPanel, java.awt.BorderLayout.CENTER);

        JButton startButton = new JButton("Iniciar Simulació");
        frame.add(startButton, java.awt.BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            simulador.iniciar();
        });

        frame.setVisible(true);
    }

    public static void simularRecorregut(Mapa mapa, MapPanel panel, List<Lloc> ruta, int delayMilisegons) {
        new Thread(() -> {
            java.util.List<Cami> caminsRecorreguts = new java.util.ArrayList<>();
            for (int i = 0; i < ruta.size() - 1; i++) {
                Lloc origen = ruta.get(i);
                Lloc desti = ruta.get(i + 1);
                for (Cami cami : mapa.getLlocs().get(origen)) {
                    if (cami.obtenirDesti().equals(desti)) {
                        caminsRecorreguts.add(cami);
                        panel.setCaminsActius(new java.util.ArrayList<>(caminsRecorreguts));
                        try {
                            Thread.sleep(delayMilisegons);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }).start();
    }
}
