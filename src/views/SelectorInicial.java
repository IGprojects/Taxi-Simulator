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

/**
 * @class SelectorInicial
 * @brief Classe que proporciona una interfície gràfica per seleccionar fitxers
 * d'entrada i definir horaris.
 *
 * @details Permet a l'usuari escollir els arxius necessaris per executar una
 * simulació, així com altres funcionalitats com visualitzar estadístiques o
 * optimitzar configuracions.
 *
 * @author Ignasi Ferres Iglesias
 * @version 2025.03.04
 */
public class SelectorInicial {

    /**
     * @interface DadesIniciListener
     * @brief Interfície per capturar els esdeveniments relacionats amb la
     * selecció de fitxers i inici de simulació.
     */
    public interface DadesIniciListener {

        /**
         * @brief Cridat quan totes les dades s'han completat i es pot iniciar
         * la simulació.
         */
        void onDadesCompletades(File mapa, File connexions, File vehicles, File conductors, File peticions, File JsonFile,
                LocalTime horaInici, LocalTime horaFinal, File EstadisticsFile);

        /**
         * @brief Cridat quan l'usuari selecciona un fitxer de simulació JSON.
         */
        void onSimulacioJsonSeleccionat(File simulacioJson);

        /**
         * @brief Cridat quan es vol optimitzar una simulació per eliminar
         * vehicles redundants.
         */
        void onOptimitzarSimulacio(File simulacioJson);

        /**
         * @brief Cridat quan es vol visualitzar estadístiques d'una simulació.
         */
        void onVisualitzarEstadistiques(File estadistiquesJson);

        /**
         * @brief Cridat quan es vol optimitzar punts de càrrega redundants a
         * partir d’una simulació.
         */
        void onOptimitzarSimulacioPuntsCarrega(File selectedFile);
    }

    /**
     * @brief Mostra la finestra de selecció de fitxers i configuració
     * d’horaris.
     *
     * @param listener Objecte que escolta les accions de l’usuari i rep els
     * arxius i horaris seleccionats.
     */
    public static void mostrar(DadesIniciListener listener) {
        JFrame frame = new JFrame("Selecció d'arxius i horaris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null);

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
            new JLabel("Simulacio.json:"),
            new JLabel("Estadistiques.json:")
        };

        JButton[] fileButtons = new JButton[7];
        File[] selectedFiles = new File[7];

        for (int i = 0; i < labels.length; i++) {
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
        gbc.gridy = 7;
        panel.add(horaIniciLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaIniciField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(horaFinalLabel, gbc);
        gbc.gridx = 1;
        panel.add(horaFinalField, gbc);

        JButton startBtn = new JButton("Iniciar simulació");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(startBtn, gbc);

        JButton testBtn = new JButton("Mode proves");
        gbc.gridy = 10;
        panel.add(testBtn, gbc);

        JButton jsonBtn = new JButton("Carregar Simulació");
        gbc.gridy = 11;
        panel.add(jsonBtn, gbc);

        JButton optimitzarBtn = new JButton("Optimitzar Simulació Vehicles Redundants");
        gbc.gridy = 12;
        panel.add(optimitzarBtn, gbc);

        JButton optimitzar2Btn = new JButton("Optimitzar Simulació Punts de Carrega Redundants");
        gbc.gridy = 13;
        panel.add(optimitzar2Btn, gbc);

        JButton veureEstadistiquesBtn = new JButton("Visualització d'Estadístiques");
        gbc.gridy = 14;
        panel.add(veureEstadistiquesBtn, gbc);

        // Mode de proves amb fitxers predefinits
        testBtn.addActionListener(e -> {
            try {
                selectedFiles[0] = new File("fitxersCSV/test3/llocs.csv");
                selectedFiles[1] = new File("fitxersCSV/test3/camins.csv");
                selectedFiles[2] = new File("fitxersCSV/test3/vehicles.csv");
                selectedFiles[3] = new File("fitxersCSV/test3/conductors.csv");
                selectedFiles[4] = new File("fitxersCSV/test3/peticions.csv");
                selectedFiles[6] = new File("fitxersCSV/test3/Estadistiques.json"); // <-- Afegit

                LocalTime horaInici = LocalTime.parse("08:00");
                LocalTime horaFinal = LocalTime.parse("20:00");

                frame.dispose();
                listener.onDadesCompletades(selectedFiles[0], selectedFiles[1], selectedFiles[2],
                        selectedFiles[3], selectedFiles[4], selectedFiles[5], horaInici, horaFinal, selectedFiles[6]);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al mode de proves: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Carregar simulació JSON
        jsonBtn.addActionListener(e -> {
            if (selectedFiles[5] == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona el fitxer simulacio.json!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            listener.onSimulacioJsonSeleccionat(selectedFiles[5]);
        });

        // Optimització de vehicles redundants
        optimitzarBtn.addActionListener(e -> {
            if (selectedFiles[5] == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona el fitxer simulacio.json!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            listener.onOptimitzarSimulacio(selectedFiles[5]);
        });

        // Optimització de punts de càrrega redundants
        optimitzar2Btn.addActionListener(e -> {
            if (selectedFiles[5] == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona el fitxer simulacio.json!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            listener.onOptimitzarSimulacioPuntsCarrega(selectedFiles[5]);
        });

        // Visualització d’estadístiques
        veureEstadistiquesBtn.addActionListener(e -> {
            if (selectedFiles[6] == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona el fitxer Estadistiques.json!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            listener.onVisualitzarEstadistiques(selectedFiles[6]);
        });

        // Inici de la simulació manual
        startBtn.addActionListener(e -> {
            // Verifiquem els 5 fitxers obligatoris (índexs 0-4)
            for (int i = 0; i < 5; i++) {
                if (selectedFiles[i] == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Error: Falten fitxers obligatoris (Llocs, Connexions, Vehicles, Conductors o Peticions).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                LocalTime horaInici = LocalTime.parse(horaIniciField.getText().trim());
                LocalTime horaFinal = LocalTime.parse(horaFinalField.getText().trim());

                frame.dispose();
                listener.onDadesCompletades(
                        selectedFiles[0], // llocs.csv
                        selectedFiles[1], // connexions.csv
                        selectedFiles[2], // vehicles.csv
                        selectedFiles[3], // conductors.csv
                        selectedFiles[4], // peticions.csv
                        selectedFiles[5], // simulacio.json (opcional)
                        horaInici,
                        horaFinal,
                        selectedFiles[6] // Estadistiques.json (opcional)
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Format d'hora incorrecte. Ha de ser HH:mm (ex: 08:00)",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
