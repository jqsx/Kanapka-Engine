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
    private Renderer renderer;
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
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((int) (screen.width / 2.0 - engineConfiguration.width / 2.0), (int) (screen.height / 2.0 - engineConfiguration.height / 2.0));

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

    public final void addRenderer(Renderer renderer) {
        add(renderer);
        this.renderer = renderer;
    }

    public static Dimension getWindowSize() {
        if (Engine.isMacOS())
            return new Dimension(main.getWidth(), main.getHeight());
        else return new Dimension((int) (main.getWidth() / 2.0), (int) (main.getHeight() / 2.0));
    }

    public final void setWorldBackdrop(Color color) {
        if (renderer != null)
            renderer.setBackground(color);
    }
}
