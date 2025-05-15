package views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SelectorInicial {

    public interface DadesIniciListener {
        void onDadesCompletades(File mapa, File connexions, File vehicles, File conductors, File peticions,File JsonFile,
                LocalTime horaInici, LocalTime horaFinal);
        
        void onSimulacioJsonSeleccionat(File simulacioJson);
    }

    public static void mostrar(DadesIniciListener listener) {
        JFrame frame = new JFrame("Selecció d'arxius i horaris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500); // Augmentem una mida la finestra
        frame.setLocationRelativeTo(null); // centrat

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel[] labels = {
                new JLabel("Llocs.csv:"),
                new JLabel("Connexions.csv:"),
                new JLabel("Vehicles.csv:"),
                new JLabel("Conductors.csv:"),
                new JLabel("Peticions.csv:"),
                new JLabel("Simulacio.json:") // Nou label pel json
        };

        JButton[] fileButtons = new JButton[6]; // Ara són 6 botons
        File[] selectedFiles = new File[6]; // Ara són 6 fitxers

        for (int i = 0; i < 6; i++) { // Ara iterem 6 vegades
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

        gbc.gridx = 0;
        gbc.gridy = 6; // Ara comença a la fila 6
        panel.add(horaIniciLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaIniciField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(horaFinalLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaFinalField, gbc);

        JButton startBtn = new JButton("Iniciar simulació");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(startBtn, gbc);

        JButton testBtn = new JButton("Mode proves");
        gbc.gridy = 9;
        panel.add(testBtn, gbc);
        
        // Nou botó per carregar només el json
        JButton jsonBtn = new JButton("Carregar Simulació");
        gbc.gridy = 10;
        panel.add(jsonBtn, gbc);

        // Botó de proves amb valors hardcoded
        testBtn.addActionListener(e -> {
            try {
                selectedFiles[0] = new File(
                        "C:\\Users\\didac\\OneDrive\\Documentos\\UDG\\2n Curs\\2n SEMESTRE\\PROPRO\\PROJECTE FINAL\\fitxersCSV\\llocs.csv");
                selectedFiles[1] = new File(
                        "C:\\Users\\didac\\OneDrive\\Documentos\\UDG\\2n Curs\\2n SEMESTRE\\PROPRO\\PROJECTE FINAL\\fitxersCSV\\camins.csv");
                selectedFiles[2] = new File(
                        "C:\\Users\\didac\\OneDrive\\Documentos\\UDG\\2n Curs\\2n SEMESTRE\\PROPRO\\PROJECTE FINAL\\fitxersCSV\\vehicles.csv");
                selectedFiles[3] = new File(
                        "C:\\Users\\didac\\OneDrive\\Documentos\\UDG\\2n Curs\\2n SEMESTRE\\PROPRO\\PROJECTE FINAL\\fitxersCSV\\conductors.csv");
                selectedFiles[4] = new File(
                        "C:\\Users\\didac\\OneDrive\\Documentos\\UDG\\2n Curs\\2n SEMESTRE\\PROPRO\\PROJECTE FINAL\\fitxersCSV\\peticions.csv");

                LocalTime horaInici = LocalTime.parse("08:00");
                LocalTime horaFinal = LocalTime.parse("12:00");

                frame.dispose();
                listener.onDadesCompletades(
                        selectedFiles[0], selectedFiles[1], selectedFiles[2], selectedFiles[3], selectedFiles[4],selectedFiles[5],
                        horaInici, horaFinal);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al mode de proves: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acció per al botó de carregar simulació
        jsonBtn.addActionListener(e -> {
            if (selectedFiles[5] == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona el fitxer simulacio.json!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            listener.onSimulacioJsonSeleccionat(selectedFiles[5]);
        });

        startBtn.addActionListener(e -> {
            for (int i = 0; i < 5; i++) { // Comprovem només els 5 primers fitxers
                if (selectedFiles[i] == null) {
                    JOptionPane.showMessageDialog(frame, "Selecciona tots els fitxers!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try {
                LocalTime horaInici = LocalTime.parse(horaIniciField.getText().trim());
                LocalTime horaFinal = LocalTime.parse(horaFinalField.getText().trim());

                frame.dispose();
                listener.onDadesCompletades(
                        selectedFiles[0], selectedFiles[1], selectedFiles[2], selectedFiles[3], selectedFiles[4],selectedFiles[5],
                        horaInici, horaFinal);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Format d'hora incorrecte. Usa HH:mm", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}