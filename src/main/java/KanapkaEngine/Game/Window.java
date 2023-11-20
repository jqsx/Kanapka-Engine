package KanapkaEngine.Game;

import KanapkaEngine.Engine;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private final EngineConfiguration engineConfiguration;
    private final Engine engine;
    private static Window main;
    public Window(EngineConfiguration engineConfiguration, Engine engine) {
        setTitle(engineConfiguration.window_title);
        setSize(engineConfiguration.width, engineConfiguration.height);
        setDefaultCloseOperation(engineConfiguration.WINDOW_CLOSE_OPERATION);
        this.engine = engine;
        this.engineConfiguration = engineConfiguration;
        main = this;

        setMinimumSize(new Dimension(300, 300));
    }

    public void addRenderer(Renderer renderer) {
        add(renderer);
        setVisible(true);
    }

    public static Dimension getWindowSize() {
        return new Dimension(main.getWidth(), main.getHeight());
    }
}
