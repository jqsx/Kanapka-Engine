package KanapkaEngine;

import KanapkaEngine.Components.Chunk;
import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.Physics;
import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.*;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class Engine {
    private boolean isRunning = true;
    private Renderer renderer;
    private Window window;
    private EngineConfiguration engineConfiguration = new EngineConfiguration();
    private final GameLogic logic;
    private Thread gameThread;
    private Thread renderThread;

    private final Physics physics = new Physics();

    private final Time time = new Time();

    private final List<Plugin> plugins = new ArrayList<>();

    private long last_fixed_update = System.nanoTime();
    private final double Second = (long) Math.pow(10, 9);

    public Engine(GameLogic logic) {
        this.logic = logic;
        init();
    }

    public Engine(GameLogic logic, EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.logic = logic;
        init();
    }

    private void credits() {
        for (int i = 0; i < 10; i++)
            System.out.print("-");
        System.out.println();
        System.out.println();
        System.out.println("Thank you for using KanapkaEngine!");
        System.out.println("Version: " + getClass().getPackage().getImplementationVersion());
        System.out.println();
        for (int i = 0; i < 10; i++)
            System.out.print("-");
        System.out.println();
    }

    private void init() {
        credits();
        window = new Window(engineConfiguration, this);

        window.addRenderer(renderer = new Renderer(engineConfiguration, this));

        init_render_thread();

        init_game_thread();

        load(new Input());
    }

    public final void load(Plugin plugin) {
        plugins.add(plugin);
        addListener(plugin);
        plugin.Apply(this);
    }

    public final void addListener(Plugin plugin) {
        if (plugin instanceof MouseWheelListener)
            renderer.addMouseWheelListener((MouseWheelListener) plugin);
        if (plugin instanceof MouseMotionListener)
            renderer.addMouseMotionListener((MouseMotionListener) plugin);
        if (plugin instanceof MouseListener)
            renderer.addMouseListener((MouseListener) plugin);
        if (plugin instanceof KeyListener)
            renderer.addKeyListener((KeyListener) plugin);
    }

    private void init_render_thread() {
        System.out.println("Initializing Render Thread.");
        renderThread = new Thread(() -> {
            while (isRunning) {
                try {
                    renderer.Render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Stopped Render Thread.");
        });
        renderThread.start();
        System.out.println("Initialized Render Thread.");
    }

    private void init_game_thread() {
        System.out.println("Initializing Game Thread.");
        logic.Start();
        gameThread = new Thread(() -> {
            while (isRunning) {
                time.GameUpdate();
                try {
                    if (last_fixed_update + Second / 50L < System.nanoTime()) {
                        double fixedDelta = (System.nanoTime() - last_fixed_update) / Second;
                        physics.FixedUpdate(fixedDelta);
                        last_fixed_update = System.nanoTime();
                    }
                    logic.Update();
                    for (int i = plugins.size() - 1; i >= 0; i--) {
                        plugins.get(i).Update();
                    }
                    Chunk.UpdateChunks();
                    try {
                        Iterator<Node> nodeIterator = SceneManager.getCurrentlyLoaded().nodes.descendingIterator();
                        while (nodeIterator.hasNext())
                            nodeIterator.next().UpdateCall();
                    } catch (ConcurrentModificationException ignore) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Stopping Game Thread.");
            try {
                logic.End();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Stopped Game Thread.");
        });
        gameThread.start();
        System.out.println("Initialized Game Thread.");
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
        RegisterRenderLayer(new ChunkNodes());
        RegisterRenderLayer(new UI());
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