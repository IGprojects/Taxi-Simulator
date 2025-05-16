package views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * @class VistaPuntsCarrega
 * @brief Vista gràfica que mostra la classificació dels punts de càrrega segons el seu ús.
 * @details Separa visualment els punts de càrrega actius i redundants, i permet eliminar aquests últims.
 * 
 * @author Ignasi Ferres Iglesias
 * @version 2025.03.04
 */
public class VistaPuntsCarrega extends JFrame {

    /**
     * @brief Constructor que genera la finestra de visualització dels punts de càrrega.
     *
     * @param puntsUsats Map amb identificadors dels punts de càrrega i el nombre de vegades que s'han utilitzat.
     */
    public VistaPuntsCarrega(Map<Integer, Integer> puntsUsats) {
        setTitle("Distribució de Punts de Càrrega");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // Panell esquerre - Punts actius (ús > 0)
        JPanel panelNoRedundants = new JPanel(new BorderLayout());
        panelNoRedundants.setBorder(BorderFactory.createTitledBorder("Punts de Càrrega Actius"));
        DefaultListModel<String> modelNoRedundants = new DefaultListModel<>();
        JList<String> llistaNoRedundants = new JList<>(modelNoRedundants);
        panelNoRedundants.add(new JScrollPane(llistaNoRedundants), BorderLayout.CENTER);

        // Panell dret - Punts redundants (ús == 0)
        JPanel panelRedundants = new JPanel(new BorderLayout());
        panelRedundants.setBorder(BorderFactory.createTitledBorder("Punts de Càrrega Redundants"));
        DefaultListModel<String> modelRedundants = new DefaultListModel<>();
        JList<String> llistaRedundants = new JList<>(modelRedundants);
        panelRedundants.add(new JScrollPane(llistaRedundants), BorderLayout.CENTER);

        // Classificació dels punts
        puntsUsats.forEach((id, usos) -> {
            String entrada = "Punt " + id + " - Usos: " + usos;
            if (usos > 0) {
                modelNoRedundants.addElement(entrada);
            } else {
                modelRedundants.addElement(entrada);
            }
        });

        // Comptadors
        panelNoRedundants.add(new JLabel("Total: " + modelNoRedundants.size()), BorderLayout.NORTH);
        panelRedundants.add(new JLabel("Total: " + modelRedundants.size()), BorderLayout.NORTH);

        // Afegir panells al frame
        add(panelNoRedundants);
        add(panelRedundants);

        // Botó per eliminar punts redundants
        JButton btnEliminar = new JButton("Eliminar Redundants");
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Eliminar " + modelRedundants.size() + " punts redundants?", 
                    "Confirmar Eliminació", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Lògica per eliminar els punts redundants
                JOptionPane.showMessageDialog(this, "Punts redundants eliminats");
                modelRedundants.clear();
                panelRedundants.add(new JLabel("Total: 0"), BorderLayout.NORTH);
            }
        });
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * @brief Mètode estàtic per mostrar la vista dels punts de càrrega.
     *
     * @param puntsUsats Map amb identificadors i usos dels punts de càrrega.
     */
    public static void mostrar(Map<Integer, Integer> puntsUsats) {
        SwingUtilities.invokeLater(() -> {
            VistaPuntsCarrega vista = new VistaPuntsCarrega(puntsUsats);
            vista.setVisible(true);
        });
    }
}
