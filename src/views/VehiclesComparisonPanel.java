
package  views;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import core.Vehicle;

public class VehiclesComparisonPanel extends JPanel {

    public VehiclesComparisonPanel(List<Vehicle> vehiclesTotals, List<Vehicle> vehiclesRedundants) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Títol del panell
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Comparació de Vehicles", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Subtítol per a vehicles totals
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel totalLabel = new JLabel("Tots els Vehicles", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(totalLabel, gbc);

        // Subtítol per a vehicles redundants
        gbc.gridx = 1;
        gbc.gridy = 1;
        JLabel redundantLabel = new JLabel("Vehicles Redundants", SwingConstants.CENTER);
        redundantLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(redundantLabel, gbc);

        // Llista de vehicles totals
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JList<Vehicle> totalList = new JList<>(new Vector<>(vehiclesTotals));
        totalList.setCellRenderer(new VehicleListRenderer());
        JScrollPane totalScrollPane = new JScrollPane(totalList);
        totalScrollPane.setPreferredSize(new Dimension(300, 400));
        add(totalScrollPane, gbc);

        // Llista de vehicles redundants
        gbc.gridx = 1;
        gbc.gridy = 2;
        JList<Vehicle> redundantList = new JList<>(new Vector<>(vehiclesRedundants));
        redundantList.setCellRenderer(new VehicleListRenderer());
        JScrollPane redundantScrollPane = new JScrollPane(redundantList);
        redundantScrollPane.setPreferredSize(new Dimension(300, 400));
        add(redundantScrollPane, gbc);

        // Informació de comptadors
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel countLabel = new JLabel(
            String.format("Total vehicles: %d | Vehicles redundants: %d (%.1f%%)",
                vehiclesTotals.size(),
                vehiclesRedundants.size(),
                vehiclesTotals.isEmpty() ? 0 : (100.0 * vehiclesRedundants.size() / vehiclesTotals.size())),
            SwingConstants.CENTER
        );
        add(countLabel, gbc);
    }

    // Renderer personalitzat per mostrar bé els vehicles a les llistes
    private static class VehicleListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) value;
                setText(String.format("Vehicle %d - %s (Autonomia: %d km)", 
                    vehicle.getId(), 
                    vehicle.getUbicacioActual().obtenirId(), 
                    vehicle.getAutonomia()));
            }
            return this;
        }
    }

    // Mètode per mostrar el panell en un JFrame
    public static void mostrarComparacio(List<Vehicle> vehiclesTotals, List<Vehicle> vehiclesRedundants) {
        JFrame frame = new JFrame("Comparació de Vehicles");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        VehiclesComparisonPanel panel = new VehiclesComparisonPanel(vehiclesTotals, vehiclesRedundants);
        frame.add(panel);
        frame.setVisible(true);
    }
}