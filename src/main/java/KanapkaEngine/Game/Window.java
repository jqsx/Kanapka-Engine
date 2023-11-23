package KanapkaEngine.Game;

import KanapkaEngine.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Window extends JFrame {
    private final EngineConfiguration engineConfiguration;
    private final Engine engine;
    private static Window main;
    public Window(EngineConfiguration engineConfiguration, Engine engine) {
        setTitle(engineConfiguration.window_title);
        setSize(engineConfiguration.width, engineConfiguration.height);
        setDefaultCloseOperation(engineConfiguration.WINDOW_CLOSE_OPERATION);

        if (engineConfiguration.custom_title_bar)
            setUndecorated(true);

//        setupCursor();

        this.engine = engine;
        this.engineConfiguration = engineConfiguration;
        main = this;

        setMinimumSize(new Dimension(300, 300));
    }

    private void setupCursor() {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        AffineTransform at = new AffineTransform();
        g.setTransform(at);
        g.setColor(Color.red);

        g.setFont(g.getFont().deriveFont(10f));
        g.drawString("Custom", 0, 10);
        g.drawString("Cursor", 0, 20);

        g.dispose();

        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "Goof");

        setCursor(cursor);
    }

    public void addRenderer(Renderer renderer) {
        add(renderer);
        setVisible(true);
    }

    public static Dimension getWindowSize() {
        return new Dimension(main.getWidth(), main.getHeight());
    }
}
