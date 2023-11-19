package KanapkaEngine;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.Renderer;
import KanapkaEngine.Game.Window;

import java.util.logging.Logger;

public class Engine {
    private static final Logger LOGGER = Logger.getLogger( Engine.class.getName() );
    private static boolean isRunning = true;
    private Renderer renderer;
    private Window window;

    private EngineConfiguration engineConfiguration = new EngineConfiguration();
    private GameLogic logic;

    private Thread gameThread;
    private Thread renderThread;

    public Engine(GameLogic logic) {
        this.logic = logic;
        init();
    }

    public Engine(GameLogic logic, EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.logic = logic;
        init();
    }

    private void init() {
        window = new Window(engineConfiguration, this);

        window.addRenderer(renderer = new Renderer(engineConfiguration, this));

        init_render_thread();

        init_game_thread();
    }

    private void init_render_thread() {
        LOGGER.info("Initializing Render Thread.");
        renderThread = new Thread(() -> {
            while (isRunning) {
                try {
                    renderer.Render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info("Stopped Render Thread.");
        });
        LOGGER.info("Initialized Render Thread.");
    }

    private void init_game_thread() {
        LOGGER.info("Initializing Game Thread.");
        logic.Start();
        gameThread = new Thread(() -> {
            while (isRunning) {
                try {
                    logic.Update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info("Stopping Game Thread.");
            try {
                logic.End();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("Stopped Game Thread.");
        });
        LOGGER.info("Initialized Game Thread.");
    }

    public long getRenderThreadID() {
        if (renderThread != null)
            return renderThread.getId();
        return -1;
    }

    public void End() {
        isRunning = false;

        window.setVisible(false);
        window.dispose();
    }

    public void RegisterRenderLayer(RenderLayer renderLayer, RenderStage renderStage) {
        renderer.RegisterRenderLayer(renderLayer, renderStage);
    }
}