import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class JCompass extends JPanel {

    private double bearing = 0.0; // This will give you angle based on North

    public JCompass() {
        setPreferredSize(new Dimension(300, 300));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D scene = (Graphics2D) g;
        scene.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 25;

        scene.setColor(new Color(40, 44, 52));
        scene.setStroke(new BasicStroke(3));
        scene.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        scene.setFont(new Font("SansSerif", Font.BOLD, 16));
        scene.drawString("N", centerX - 6, centerY - radius + 20);
        scene.drawString("S", centerX - 5, centerY + radius - 10);
        scene.drawString("E", centerX + radius - 20, centerY + 6);
        scene.drawString("W", centerX - radius + 10, centerY + 6);

        AffineTransform oldTransform = scene.getTransform();

        scene.translate(centerX, centerY);
        scene.rotate(Math.toRadians(bearing));

        int needleLength = radius - 35;
        int[] xNorth = {0, -10, 10};
        int[] yNorth = {-needleLength, 0, 0};
        scene.setColor(Color.RED);
        scene.fillPolygon(xNorth, yNorth, 3);

        int[] xSouth = {0, -10, 10};
        int[] ySouth = {needleLength, 0, 0};
        scene.setColor(Color.GRAY);
        scene.fillPolygon(xSouth, ySouth, 3);

        scene.setTransform(oldTransform);

        scene.setColor(Color.BLACK);
        scene.fillOval(centerX - 6, centerY - 6, 12, 12);
        scene.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }
}
