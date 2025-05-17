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
 * @class EstadistiquesPanel
 * @brief Panell principal que conté diverses gràfiques estadístiques.
 * @author Ignasi Ferres Iglesias
 */
public class EstadistiquesPanel extends JPanel {

    /** @brief Map amb les estadístiques de cada simulació. */
    private final Map<String, Estadistiques> dades;

    /**
     * @brief Constructor del panell d'estadístiques.
     * @param dades Map amb els noms de les simulacions i les seves estadístiques corresponents.
     */
    public EstadistiquesPanel(Map<String, Estadistiques> dades) {
        this.dades = dades;
        setLayout(new GridLayout(3, 2, 10, 10));
        setBackground(new Color(240, 240, 240)); // Fons clar
        afegirGrafics();
    }

    /**
     * @brief Afegeix les diferents gràfiques al panell.
     */
    private void afegirGrafics() {
        add(new Grafica("Peticions Servides", dades, est -> (double) est.getPeticionesServidas()));
        add(new Grafica("Temps Total Espera (min)", dades, Estadistiques::getTiempoEsperaPromedio));
        add(new Grafica("Ocupació Vehicles (%)", dades, Estadistiques::getOcupacionPromedioVehiculos));
        add(new Grafica("Bateria Promig (%)", dades, Estadistiques::getPorcentajeBateriaPromedio));
        add(new Grafica("Temps Total Viatge (min)", dades, Estadistiques::getTiempoViajePromedio));
    }

    /**
     * @class Grafica
     * @brief Classe interna que representa una gràfica individual.
     */
    private static class Grafica extends JPanel {

        /** @brief Títol de la gràfica. */
        private final String titol;

        /** @brief Dades estadístiques per pintar. */
        private final Map<String, Estadistiques> dades;

        /** @brief Funció per obtenir un valor específic d'una estadística. */
        private final ValGetter getter;

        /** @brief Colors per diferenciar les sèries. */
        private final Color[] colors = {
            new Color(55, 126, 184),   // Blau
            new Color(228, 26, 28),    // Vermell
            new Color(77, 175, 74),    // Verd
            new Color(152, 78, 163),   // Lila
            new Color(255, 127, 0),    // Taronja
            new Color(166, 86, 40)     // Marró
        };

        /**
         * @brief Constructor de la gràfica.
         * @param titol Títol de la gràfica.
         * @param dades Map de dades estadístiques.
         * @param getter Funció per obtenir el valor específic a representar.
         */
        public Grafica(String titol, Map<String, Estadistiques> dades, ValGetter getter) {
            this.titol = titol;
            this.dades = dades;
            this.getter = getter;
            setPreferredSize(new Dimension(500, 350));
            setBackground(Color.WHITE);
            setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }

        /**
         * @brief Pinta el contingut de la gràfica.
         * @param g Objecte gràfic proporcionat pel sistema.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int labelPadding = 40;
            int graphWidth = width - 2 * padding - labelPadding;
            int graphHeight = height - 2 * padding;

            // Dibuixar títol
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(60, 60, 60));
            g2.drawString(titol, padding, padding - 15);

            // Calcular valors
            List<Double> valors = dades.values().stream().map(getter::get).toList();
            double maxValor = valors.stream().mapToDouble(v -> v).max().orElse(1);
            double minValor = valors.stream().mapToDouble(v -> v).min().orElse(0);
            if (minValor > 0) {
                minValor = 0;
            }
            maxValor *= 1.1; // Per marge visual

            // Dibuixar graella horitzontal
            int x0 = padding + labelPadding;
            int y0 = padding;
            int xStep = graphWidth / Math.max(dades.size() - 1, 1);
            int liniesY = 5;

            g2.setColor(new Color(220, 220, 220));
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= liniesY; i++) {
                int y = y0 + (i * graphHeight / liniesY);
                g2.drawLine(x0, y, x0 + graphWidth, y);
                double val = maxValor - (i * (maxValor - minValor) / liniesY);
                g2.drawString(String.format("%.1f", val), padding, y + 4);
            }

            // Coordenades dels punts
            int[] xs = new int[dades.size()];
            int[] ys = new int[dades.size()];
            int i = 0;
            for (Map.Entry<String, Estadistiques> entry : dades.entrySet()) {
                double val = getter.get(entry.getValue());
                int x = x0 + i * xStep;
                int y = y0 + (int) ((1 - (val - minValor) / (maxValor - minValor)) * graphHeight);
                xs[i] = x;
                ys[i] = y;
                i++;
            }

            // Línia entre punts
            g2.setColor(new Color(100, 100, 100, 70));
            g2.setStroke(new BasicStroke(2.5f));
            for (int j = 0; j < xs.length - 1; j++) {
                g2.drawLine(xs[j], ys[j], xs[j + 1], ys[j + 1]);
            }

            // Dibuixar punts i etiquetes
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            i = 0;
            for (Map.Entry<String, Estadistiques> entry : dades.entrySet()) {
                g2.setColor(colors[i % colors.length]);
                g2.fillOval(xs[i] - 6, ys[i] - 6, 12, 12);
                int yOffset = (i % 2 == 0) ? -18 : 20;
                String label = String.format("%s: %.1f",
                        entry.getKey().replace("Simulació ", "S"),
                        getter.get(entry.getValue()));
                g2.drawString(label, xs[i] - 15, ys[i] + yOffset);
                i++;
            }

            // Dibuixar eixos
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x0, y0, x0, y0 + graphHeight);
            g2.drawLine(x0, y0 + graphHeight, x0 + graphWidth, y0 + graphHeight);

            // Dibuixar llegenda
            int legendX = width - padding - 120;
            int legendY = padding + 20;
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Llegenda:", legendX, legendY);

            i = 0;
            for (Map.Entry<String, Estadistiques> entry : dades.entrySet()) {
                g2.setColor(colors[i % colors.length]);
                g2.fillRect(legendX, legendY + 15 + i * 20, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), legendX + 18, legendY + 25 + i * 20);
                i++;
            }
        }

        /**
         * @interface ValGetter
         * @brief Interfície funcional per obtenir un valor de tipus double d'un objecte Estadistiques.
         */
        interface ValGetter {
            /**
             * @brief Obté el valor de l'estadística concreta.
             * @param est L'objecte Estadistiques.
             * @return El valor a representar a la gràfica.
             */
            double get(Estadistiques est);
        }
    }
}
