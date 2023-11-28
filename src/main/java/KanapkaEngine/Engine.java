package KanapkaEngine;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.Chunks;
import KanapkaEngine.RenderLayers.Debug;
import KanapkaEngine.RenderLayers.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    private static final Logger LOGGER = LogManager.getLogger(Engine.class);
    private static boolean isRunning = true;
    private Renderer renderer;
    private Window window;

    private EngineConfiguration engineConfiguration = new EngineConfiguration();
    private GameLogic logic;

    private Thread gameThread;
    private Thread renderThread;

    private Time time = new Time();

    private List<Plugin> plugins = new ArrayList<>();

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

    public final void load(Plugin plugin) {
        plugins.add(plugin);
        plugin.Apply(this);
    }

    public final void addListener(Plugin plugin) {
        if (plugin instanceof MouseWheelListener listener)
            window.addMouseWheelListener(listener);
        if (plugin instanceof MouseMotionListener listener)
            window.addMouseMotionListener(listener);
        if (plugin instanceof MouseListener listener)
            window.addMouseListener(listener);
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
        renderThread.start();
        LOGGER.info("Initialized Render Thread.");
    }

    private void init_game_thread() {
        LOGGER.info("Initializing Game Thread.");
        logic.Start();
        gameThread = new Thread(() -> {
            while (isRunning) {
                time.GameUpdate();
                try {
                    logic.Update();
                    for (int i = 0; i < plugins.size(); i++) {
                        plugins.get(i).Update();
                    }
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
        gameThread.start();
        LOGGER.info("Initialized Game Thread.");
    }

    public final long getRenderThreadID() {
        if (renderThread != null)
            return renderThread.getId();
        return -1;
    }

    public final long getGameThreadID() {
        if (gameThread != null)
            return gameThread.getId();
        return -1;
    }

    public final Window getWindow() {
        return window;
    }

    public final void End() {
        isRunning = false;

        window.setVisible(false);
        window.dispose();

        for (Plugin plugin : plugins) {
            plugin.Detach();
        }
    }

    public final void RegisterRenderLayer(RenderLayer renderLayer) {
        renderer.RegisterRenderLayer(renderLayer, renderLayer.getStage());
    }

    public final void InitializeLayers() {
        RegisterRenderLayer(new Chunks());
        RegisterRenderLayer(new Debug());
        RegisterRenderLayer(new World());
    }

    /**
     * There are inconsistencies when it comes to window size on a mac and windows computers. Mac window size get calls are 2x smaller for some reason.
     * @return If the operating system is a mac.
     */
    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}