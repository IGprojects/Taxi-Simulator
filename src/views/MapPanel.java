package myapp;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class MapPanel extends JPanel {

    private final Mapa mapa;
    private List<Cami> caminsActius = new java.util.ArrayList<>();

    public MapPanel(Mapa mapa) {
        this.mapa = mapa;
        setBackground(Color.WHITE);
    }

    public void setCaminsActius(List<Cami> camins) {
        this.caminsActius = camins;
        repaint();
    }

    //pintar una aresta faltara ficar delays per fer afecte de pas entre diferentes arestes
    public void destacarCamiEntre(int idOrigen, int idDesti) {
        for (Map.Entry<Lloc, List<Cami>> entry : mapa.getLlocs().entrySet()) {
            for (Cami cami : entry.getValue()) {
                if ((cami.obtenirOrigen().getId() == idOrigen && cami.obtenirDesti().getId() == idDesti) ||
                    (cami.obtenirOrigen().getId() == idDesti && cami.obtenirDesti().getId() == idOrigen)) {
    
                    if (!caminsActius.contains(cami)) {
                        caminsActius.add(cami);
                        repaint();
                    }
                    return;
                }
            }
        }
    
        System.out.println("Cap camí trobat entre " + idOrigen + " i " + idDesti);
    }
    


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuixar camins
        for (Map.Entry<Lloc, List<Cami>> entry : mapa.getLlocs().entrySet()) {
            Lloc origen = entry.getKey();
            for (Cami cami : entry.getValue()) {
                Lloc desti = cami.obtenirDesti();

                int x1 = origen.getX();
                int y1 = origen.getY();
                int x2 = desti.getX();
                int y2 = desti.getY();

                if (caminsActius.contains(cami)) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(Color.GRAY);
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawLine(x1 + 25, y1 + 25, x2 + 25, y2 + 25);
            }
        }

        // Dibuixar llocs
        for (Lloc lloc : mapa.getLlocs().keySet()) {
            int x = lloc.getX();
            int y = lloc.getY();
            int size = 50;

            if (lloc.obtenirCapacitatMaxima() > 0) {
                g2.setColor(new Color(200, 0, 0));
                g2.fillRect(x, y, size, size);
            } else {
                g2.setColor(new Color(30, 144, 255));
                g2.fillOval(x, y, size, size);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            String text = String.valueOf(lloc.getId());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2.drawString(text, x + (size - textWidth) / 2, y + (size + textHeight / 2) / 2);
        }
    }
}
