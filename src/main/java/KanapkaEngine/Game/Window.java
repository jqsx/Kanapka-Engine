package KanapkaEngine.Game;

import KanapkaEngine.Engine;

import javax.swing.*;

public class Window extends JFrame {
    private final EngineConfiguration engineConfiguration;
    private final Engine engine;
    public Window(EngineConfiguration engineConfiguration, Engine engine) {
        setTitle(engineConfiguration.window_title);
        setSize(engineConfiguration.width, engineConfiguration.height);
        setDefaultCloseOperation(engineConfiguration.WINDOW_CLOSE_OPERATION);
        this.engine = engine;
        this.engineConfiguration = engineConfiguration;
    }

    public void addRenderer(Renderer renderer) {
        add(renderer);
        setVisible(true);
    }
}
