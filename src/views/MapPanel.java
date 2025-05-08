package views;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;

import core.Cami;
import core.Lloc;
import core.Mapa;
import core.Parquing;
import core.Vehicle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;

public class MapPanel extends JPanel {

    private final Mapa mapa;
    private final Map<Vehicle, List<Cami>> caminsPerVehicle = new HashMap<>();
    private final Map<Vehicle, Color> colorPerVehicle = new HashMap<>();
    private final Map<Lloc, Point> posicions = new HashMap<>();
    private Lloc llocSeleccionat = null;
    private Point offset = null;
    private String horaActual = "00:00";

    public MapPanel(Mapa mapa) {
        this.mapa = mapa;
        setBackground(Color.WHITE);
        calcularPosicionsAutomatiques();
        configurarMouseListeners();
    }

    private Color generarColor(int index) {
        float hue = (index * 0.6180339887f) % 1.0f; // nombre irracional per bona separació
        return Color.getHSBColor(hue, 0.8f, 0.9f);
    }

    public void afegirCamiPerVehicle(Vehicle vehicle, Cami cami) {
        caminsPerVehicle.computeIfAbsent(vehicle, v -> new ArrayList<>()).add(cami);

        if (!colorPerVehicle.containsKey(vehicle)) {
            colorPerVehicle.put(vehicle, generarColor(colorPerVehicle.size()));
        }

        repaint();
    }

    private void configurarMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point click = e.getPoint();
                for (Map.Entry<Lloc, Point> entry : posicions.entrySet()) {
                    Point p = entry.getValue();
                    Rectangle bounds = new Rectangle(p.x, p.y, 50, 50);
                    if (bounds.contains(click)) {
                        llocSeleccionat = entry.getKey();
                        offset = new Point(click.x - p.x, click.y - p.y);
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                llocSeleccionat = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (llocSeleccionat != null) {
                    Point novaPos = e.getPoint();
                    posicions.put(llocSeleccionat, new Point(novaPos.x - offset.x, novaPos.y - offset.y));
                    repaint();
                }
            }
        });
    }

    public void animarCami(Cami cami, Vehicle vehicle) {
        final int[] progress = { 0 }; // 0 → 100
        Timer timer = new Timer(100, null); // cada 100 ms

        timer.addActionListener(e -> {
            progress[0] += 20; // puja un 20% cada cop

            if (progress[0] >= 100) {
                // Afegeix camí complet al final
                caminsPerVehicle.computeIfAbsent(vehicle, v -> new ArrayList<>()).add(cami);
                repaint();
                ((Timer) e.getSource()).stop();
            } else {
                // Pintar només parcial
                pintarCamiParcial(cami, vehicle, progress[0] / 100.0);
            }
        });

        timer.start();
    }

    private void pintarCamiParcial(Cami cami, Vehicle vehicle, double percentatge) {
        Graphics2D g2 = (Graphics2D) getGraphics();
        Color color = colorPerVehicle.getOrDefault(vehicle, Color.BLACK);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(4));

        Point p1 = posicions.get(cami.obtenirOrigen());
        Point p2 = posicions.get(cami.obtenirDesti());

        int x1 = p1.x + 25, y1 = p1.y + 25;
        int x2 = p2.x + 25, y2 = p2.y + 25;

        int xParcial = (int) (x1 + (x2 - x1) * percentatge);
        int yParcial = (int) (y1 + (y2 - y1) * percentatge);

        g2.drawLine(x1, y1, xParcial, yParcial);
    }

    public void assignarColorVehicle(Vehicle v) {
        Random rand = new Random();
        Color colorAleatori = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        colorPerVehicle.put(v, colorAleatori);
    }

    private void calcularPosicionsAutomatiques() {
        int margin = 100;
        int separacio = 120;
        int cols = (int) Math.ceil(Math.sqrt(mapa.getLlocs().size()));
        int i = 0;

        for (Lloc lloc : mapa.getLlocs().keySet()) {
            int row = i / cols;
            int col = i % cols;
            int x = margin + col * separacio;
            int y = margin + row * separacio;
            posicions.put(lloc, new Point(x, y));
            i++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuixar camins
        // 1. Pintar tots els camins normals en ORANGE (per defecte)
        for (Map.Entry<Lloc, List<Cami>> entry : mapa.getLlocs().entrySet()) {
            Lloc origen = entry.getKey();
            for (Cami cami : entry.getValue()) {
                Lloc desti = cami.obtenirDesti();

                Point p1 = posicions.get(origen);
                Point p2 = posicions.get(desti);
                int x1 = p1.x, y1 = p1.y;
                int x2 = p2.x, y2 = p2.y;

                g2.setColor(Color.ORANGE);
                g2.setStroke(new BasicStroke(2));
                drawArrow(g2, x1 + 25, y1 + 25, x2 + 25, y2 + 25);
            }
        }

        // 2. Pintar camins actius per vehicle (colors únics)
        for (Map.Entry<Vehicle, List<Cami>> entry : caminsPerVehicle.entrySet()) {
            Vehicle vehicle = entry.getKey();
            Color color = colorPerVehicle.getOrDefault(vehicle, Color.BLACK);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(4));

            for (Cami cami : entry.getValue()) {
                Point p1 = posicions.get(cami.obtenirOrigen());
                Point p2 = posicions.get(cami.obtenirDesti());
                drawLiniaContinua(g2, p1.x + 25, p1.y + 25, p2.x + 25, p2.y + 25);
            }
        }

        // Dibuixar llocs
        for (Lloc lloc : mapa.getLlocs().keySet()) {
            Point p = posicions.get(lloc);
            int x = p.x, y = p.y;

            int size = 50;

            if (lloc instanceof Parquing) {
                g2.setColor(new Color(200, 0, 0));
                g2.fillRect(x, y, size, size);
            } else {
                g2.setColor(new Color(30, 144, 255));
                g2.fillOval(x, y, size, size);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            String text = String.valueOf(lloc.obtenirId());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2.drawString(text, x + (size - textWidth) / 2, y + (size + textHeight / 2) / 2);
        }

        // Dibuixar hora actual (dalt a la dreta)
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(horaActual);
        g2.drawString("Hora: " + horaActual, getWidth() - textWidth - 60, 30);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int arrowSize = 10;
        int nodeRadius = 25;

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        double length = Math.sqrt(dx * dx + dy * dy);

        // Retallem la línia perquè no entri dins el node
        double cutLength = nodeRadius;
        int newX2 = (int) (x1 + (length - cutLength) * Math.cos(angle));
        int newY2 = (int) (y1 + (length - cutLength) * Math.sin(angle));

        // Línia discontínua
        Stroke originalStroke = g2.getStroke();
        float[] dashPattern = { 10, 10 }; // 10 píxels pintats, 10 píxels en blanc
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        g2.drawLine(x1, y1, newX2, newY2);
        g2.setStroke(originalStroke); // Tornar a l'estil anterior

        // Dibuixa el cap de fletxa (ple normal)
        int xArrow1 = (int) (newX2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (newY2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (newX2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (newY2 - arrowSize * Math.sin(angle + Math.PI / 6));

        int[] xPoints = { newX2, xArrow1, xArrow2 };
        int[] yPoints = { newY2, yArrow1, yArrow2 };
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawLiniaContinua(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int arrowSize = 10;
        int nodeRadius = 25;

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        double length = Math.sqrt(dx * dx + dy * dy);

        // Retallem la línia perquè no entri dins el node
        double cutLength = nodeRadius;
        int newX2 = (int) (x1 + (length - cutLength) * Math.cos(angle));
        int newY2 = (int) (y1 + (length - cutLength) * Math.sin(angle));

        // Dibuixa la línia fins abans del node destí
        g2.drawLine(x1, y1, newX2, newY2);

        // Dibuixa el cap de fletxa
        int xArrow1 = (int) (newX2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (newY2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (newX2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (newY2 - arrowSize * Math.sin(angle + Math.PI / 6));

        int[] xPoints = { newX2, xArrow1, xArrow2 };
        int[] yPoints = { newY2, yArrow1, yArrow2 };
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    public void setHoraActual(LocalTime hora) {
        this.horaActual = hora.toString();
        repaint();
    }

}
