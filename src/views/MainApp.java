package myapp;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectorInicial.mostrar((mapaFile, connexionsFile, vehicles, conductors, peticions, horaInici, horaFinal) -> {
                carregarIMostrarMapa(mapaFile, connexionsFile);
            });
        });
    }

    private static void carregarIMostrarMapa(File mapaFile, File connexionsFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapaFile));
            String linea = reader.readLine();
            String[] valors = linea.split(" ");
            int nLlocs = Integer.parseInt(valors[0]);
            int nConnexions = Integer.parseInt(valors[1]);

            Mapa mapa = new Mapa(nLlocs, nConnexions);

            // Llegir llocs
            for (int i = 0; i < nLlocs; i++) {
                linea = reader.readLine();
                valors = linea.split(";");
                int id = Integer.parseInt(valors[0]);
                int capacitat = Integer.parseInt(valors[1]);
                int x = Integer.parseInt(valors[2]);
                int y = Integer.parseInt(valors[3]);
                mapa.afegirLloc(new Lloc(id, capacitat, x, y));
            }
            reader.close();

            // Llegir connexions
            reader = new BufferedReader(new FileReader(connexionsFile));
            while ((linea = reader.readLine()) != null) {
                valors = linea.split(";");
                int origenId = Integer.parseInt(valors[0]);
                int destiId = Integer.parseInt(valors[1]);
                double distancia = Double.parseDouble(valors[2]);
                double temps = Double.parseDouble(valors[3]);

                Lloc origen = mapa.getLlocPerId(origenId);
                Lloc desti = mapa.getLlocPerId(destiId);
                if (origen != null && desti != null) {
                    mapa.afegirCami(new Cami(origen, desti, distancia, temps));
                }
            }
            reader.close();

            mostrarMapa(mapa);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error carregant fitxers: " + e.getMessage());
        }
    }

    private static void mostrarMapa(Mapa mapa) {
        JFrame frame = new JFrame("Visualització del Mapa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        MapPanel mapPanel = new MapPanel(mapa);
        frame.setLayout(new java.awt.BorderLayout());
        frame.add(mapPanel, java.awt.BorderLayout.CENTER);

        JButton startButton = new JButton("Iniciar Recorregut");
        frame.add(startButton, java.awt.BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            Lloc origen = mapa.getLlocPerId(0);
            Lloc desti = mapa.getLlocPerId(3);

            if (origen != null && desti != null) {
                List<Lloc> ruta = mapa.camiVoraç(origen, desti);
                simularRecorregut(mapa, mapPanel, ruta, 1000);
            } else {
                JOptionPane.showMessageDialog(frame, "Origen o destí no trobats.");
            }
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
