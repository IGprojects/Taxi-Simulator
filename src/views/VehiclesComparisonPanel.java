package views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * @class VehiclesComparisonPanel
 * @brief Panell gràfic que mostra una comparació entre conductors actius i redundants.
 * @details A l'esquerra es mostren conductors amb almenys una petició servida, i a la dreta els que no n'han servit cap.
 * @author Ignasi Ferres Iglesias
 */
public class VehiclesComparisonPanel extends JPanel {

    /**
     * @brief Constructor que construeix el panell segons les dades de peticions per conductor.
     * @param conductorsRedundants Map on la clau és l'ID del conductor i el valor el nombre de peticions servides.
     */
    public VehiclesComparisonPanel(Map<Integer, Integer> conductorsRedundants) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Títol
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Comparació de Conductors", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel actiusLabel = new JLabel("Conductors Actius", SwingConstants.CENTER);
        actiusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(actiusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JLabel redundantsLabel = new JLabel("Conductors Redundants", SwingConstants.CENTER);
        redundantsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(redundantsLabel, gbc);

        // Separació de dades
        List<String> actius = conductorsRedundants.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .map(e -> String.format("Conductor %d - %d peticions", e.getKey(), e.getValue()))
            .collect(Collectors.toList());

        List<String> redundants = conductorsRedundants.entrySet().stream()
            .filter(e -> e.getValue() == 0)
            .map(e -> String.format("Conductor %d - 0 peticions", e.getKey()))
            .collect(Collectors.toList());

        // Llistes
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JList<String> actiusList = new JList<>(new Vector<>(actius));
        JScrollPane scrollActius = new JScrollPane(actiusList);
        scrollActius.setPreferredSize(new Dimension(300, 400));
        add(scrollActius, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JList<String> redundantsList = new JList<>(new Vector<>(redundants));
        JScrollPane scrollRedundants = new JScrollPane(redundantsList);
        scrollRedundants.setPreferredSize(new Dimension(300, 400));
        add(scrollRedundants, gbc);

        // Resum
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel countLabel = new JLabel(
            String.format("Total conductors: %d | Redundants: %d (%.1f%%)",
                conductorsRedundants.size(),
                redundants.size(),
                conductorsRedundants.isEmpty() ? 0 : (100.0 * redundants.size() / conductorsRedundants.size())
            ),
            SwingConstants.CENTER
        );
        add(countLabel, gbc);
    }

    /**
     * @brief Mètode per mostrar la comparació de conductors en una finestra separada.
     * @param conductorsRedundants Map amb ID de conductor i peticions fetes.
     */
    public static void mostrarComparacio(Map<Integer, Integer> conductorsRedundants) {
        JFrame frame = new JFrame("Comparació de Conductors");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        VehiclesComparisonPanel panel = new VehiclesComparisonPanel(conductorsRedundants);
        frame.add(panel);
        frame.setVisible(true);
    }
}
