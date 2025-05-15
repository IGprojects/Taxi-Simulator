package views;

import javax.swing.*;
import java.awt.*;

/**
 * @class LegendPanel
 * @brief Classe que representa el panell de llegenda de la simulació.
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.13
 */
public class LegendPanel extends JPanel {

    public LegendPanel() {
        setPreferredSize(new Dimension(0, 50));
        setBackground(Color.WHITE);
    }

    /**
     * @pre g != null
     * @post Pintem el panell de llegenda
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int x = 20;
        int y = 20;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Quadrat: Depot
        g2.setColor(new Color(100, 100, 255));
        g2.drawRect(x, y - 12, 20, 20);
        g2.drawString("Depot", x + 30, y + 3);

        // Cercle: Location
        x += 120;
        g2.setColor(new Color(100, 180, 255));
        g2.drawOval(x, y - 12, 20, 20);
        g2.drawString("Location", x + 30, y + 3);

        // Línia discontínua taronja: Request
        x += 150;
        Stroke originalStroke = g2.getStroke();
        float[] dash = {8f, 8f};
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g2.setColor(new Color(255, 140, 0));
        g2.drawLine(x, y, x + 40, y);
        g2.setStroke(originalStroke);
        g2.setColor(Color.BLACK);
        g2.drawString("Paths", x + 50, y + 5);

        // Línia contínua negra: Vehicle route
        x += 140;
        g2.setColor(Color.BLACK);
        g2.drawLine(x, y, x + 40, y);
        // Fletxa
        int[] xPoints = {x + 40, x + 35, x + 35};
        int[] yPoints = {y, y - 5, y + 5};
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.drawString("Vehicle Route", x + 50, y + 5);
    }
}
