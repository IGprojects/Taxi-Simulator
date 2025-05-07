package myapp;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class SelectorInicial {

    public interface DadesIniciListener {
        void onDadesCompletades(File mapa, File connexions, File vehicles, File conductors, File peticions, String horaInici, String horaFinal);
    }

    public static void mostrar(DadesIniciListener listener) {
        JFrame frame = new JFrame("Selecció d'arxius i horaris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // centrat

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel[] labels = {
            new JLabel("Mapa.csv:"),
            new JLabel("Connexions.csv:"),
            new JLabel("Vehicles.csv:"),
            new JLabel("Conductors.csv:"),
            new JLabel("Peticions.csv:")
        };

        JButton[] fileButtons = new JButton[5];
        File[] selectedFiles = new File[5];

        for (int i = 0; i < 5; i++) {
            int idx = i;
            fileButtons[i] = new JButton("Selecciona fitxer...");
            fileButtons[i].addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    selectedFiles[idx] = chooser.getSelectedFile();
                    fileButtons[idx].setText(selectedFiles[idx].getName());
                }
            });
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(labels[i], gbc);
            gbc.gridx = 1;
            panel.add(fileButtons[i], gbc);
        }

        JLabel horaIniciLabel = new JLabel("Hora inici (HH:mm):");
        JLabel horaFinalLabel = new JLabel("Hora final (HH:mm):");
        JTextField horaIniciField = new JTextField();
        JTextField horaFinalField = new JTextField();

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(horaIniciLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaIniciField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(horaFinalLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaFinalField, gbc);

        JButton startBtn = new JButton("Iniciar simulació");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(startBtn, gbc);

        startBtn.addActionListener(e -> {
            for (int i = 0; i < 5; i++) {
                if (selectedFiles[i] == null) {
                    JOptionPane.showMessageDialog(frame, "Selecciona tots els fitxers!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String horaInici = horaIniciField.getText().trim();
            String horaFinal = horaFinalField.getText().trim();
            if (!horaInici.matches("\\d{2}:\\d{2}") || !horaFinal.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "Format d'hora incorrecte. Usa HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            frame.dispose(); // Tanca aquesta finestra
            listener.onDadesCompletades(selectedFiles[0], selectedFiles[1], selectedFiles[2], selectedFiles[3], selectedFiles[4], horaInici, horaFinal);
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
