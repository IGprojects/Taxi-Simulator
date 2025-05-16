package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import core.Estadistiques;

/**
 * @author Ignasi Ferres Iglesias
 * @brief Panell que mostra diversos gràfics de línia amb estadístiques de funcionament.
 */
public class EstadistiquesPanel extends JPanel {

    private final Map<String, Estadistiques> dades;

    /**
     * @brief Constructor que rep les estadístiques i crea el layout de gràfics.
     * @param dades Mapa amb identificadors (clau) i objectes Estadistiques.
     */
    public EstadistiquesPanel(Map<String, Estadistiques> dades) {
        this.dades = dades;
        setLayout(new GridLayout(3, 2, 10, 10)); ///< Configura un layout de 3 files i 2 columnes
        afegirGrafics();
    }

    /**
     * @brief Afegeix diferents gràfics amb diverses mètriques al panell.
     */
    private void afegirGrafics() {
        add(new Grafica("Peticions Servides", dades, est -> (double) est.getPeticionesServidas()));
        add(new Grafica("Temps Total Espera", dades, Estadistiques::getTiempoEsperaPromedio));
        add(new Grafica("Ocupació Total Vehicles", dades, Estadistiques::getOcupacionPromedioVehiculos));
        add(new Grafica("Bateria Promig (%)", dades, Estadistiques::getPorcentajeBateriaPromedio));
        add(new Grafica("Temps Total Viatge", dades, Estadistiques::getTiempoViajePromedio));
    }

    /**
     * @class Grafica
     * @brief Component que representa un gràfic de línia d'una mètrica estadística.
     */
    private static class Grafica extends JPanel {

        private final String titol;
        private final Map<String, Estadistiques> dades;
        private final ValGetter getter;

        /**
         * @brief Constructor de la classe Grafica.
         * @param titol Títol del gràfic.
         * @param dades Dades a representar.
         * @param getter Funció per obtenir el valor numèric de l'objecte Estadistiques.
         */
        public Grafica(String titol, Map<String, Estadistiques> dades, ValGetter getter) {
            this.titol = titol;
            this.dades = dades;
            this.getter = getter;
            setPreferredSize(new Dimension(400, 250)); ///< Mida predeterminada del gràfic
        }

        /**
         * @brief Dibuixa el gràfic, eixos, punts, línies i etiquetes.
         * @param g Objecte gràfic per pintar.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int labelPadding = 30;
            int graphWidth = width - 2 * padding - labelPadding;
            int graphHeight = height - 2 * padding;

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString(titol, padding, padding - 10);

            List<Double> valors = dades.values().stream().map(getter::get).toList();
            double maxValor = valors.stream().mapToDouble(v -> v).max().orElse(1);
            double minValor = 0;

            int x0 = padding + labelPadding;
            int y0 = padding;
            int xStep = graphWidth / Math.max(dades.size() - 1, 1);

            int liniesY = 5;
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= liniesY; i++) {
                int y = y0 + (i * graphHeight / liniesY);
                g2.drawLine(x0, y, x0 + graphWidth, y);
                double val = maxValor - (i * (maxValor - minValor) / liniesY);
                g2.drawString(String.format("%.1f", val), padding, y + 4);
            }

            int[] xs = new int[dades.size()];
            int[] ys = new int[dades.size()];
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN};

            int i = 0;
            for (Map.Entry<String, Estadistiques> entry : dades.entrySet()) {
                double val = getter.get(entry.getValue());
                int x = x0 + i * xStep;
                int y = y0 + (int) ((1 - (val - minValor) / (maxValor - minValor)) * graphHeight);
                xs[i] = x;
                ys[i] = y;
                i++;
            }

            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2));
            for (int j = 0; j < xs.length - 1; j++) {
                g2.drawLine(xs[j], ys[j], xs[j + 1], ys[j + 1]);
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            i = 0;
            for (Map.Entry<String, Estadistiques> entry : dades.entrySet()) {
                g2.setColor(colors[i % colors.length]);
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                String label = entry.getKey() + " (" + String.format("%.2f", getter.get(entry.getValue())) + ")";
                g2.drawString(label, xs[i] - 20, ys[i] - 10);
                i++;
            }

            g2.setColor(Color.BLACK);
            g2.drawLine(x0, y0, x0, y0 + graphHeight);           ///< Eix Y
            g2.drawLine(x0, y0 + graphHeight, x0 + graphWidth, y0 + graphHeight); ///< Eix X
        }

        /**
         * @interface ValGetter
         * @brief Interfície funcional per obtenir un valor de l'objecte Estadistiques.
         */
        interface ValGetter {
            /**
             * @brief Retorna un valor doble des de l'objecte Estadistiques.
             * @param est Objecte Estadistiques.
             * @return Valor numèric.
             */
            double get(Estadistiques est);
        }
    }
}
