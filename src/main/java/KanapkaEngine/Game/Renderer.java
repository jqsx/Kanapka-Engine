package KanapkaEngine.Game;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Engine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends Canvas {
    private final EngineConfiguration engineConfiguration;
    private final Engine engine;
    private final List<RenderLayer> BACKGROUND = new ArrayList<>();
    private final List<RenderLayer> WORLD = new ArrayList<>();
    private final List<RenderLayer> PARTICLES = new ArrayList<>();
    private final List<RenderLayer> UI = new ArrayList<>();
    private final List<RenderLayer> FOREGROUND = new ArrayList<>();

    long render_frame_delay = System.nanoTime();

    private final long Second = (long) Math.pow(10, 9);

    private static int FPS = 0;

    private Dimension target_size;

    public Renderer(EngineConfiguration engineConfiguration, Engine engine) {
        this.engineConfiguration = engineConfiguration;
        this.engine = engine;
        target_size = engineConfiguration.target_size;
        setBackground(Color.black);
        setSize(320, 180);
    }

    public void Render() {
        if (engine.getRenderThreadID() != Thread.currentThread().getId()) return;
        if (render_frame_delay + Second / engineConfiguration.FPSLIMIT > System.nanoTime()) return;
        double delta = (System.nanoTime() - render_frame_delay) / (double) Second;
        FPS = (int) (1.0 / delta);
        render_frame_delay = System.nanoTime();
        Frame();
    }

    public static int getFPS() {
        return FPS;
    }

    private void Frame() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }

        Graphics2D main = (Graphics2D) bs.getDrawGraphics();
        main.setColor(getBackground());
        main.fillRect(0, 0, getWidth(), getHeight());

        main.setTransform(getWorldTransform());
        main.setColor(Color.red);
        main.fillRect(-100, -100, 200, 200);
        Render_Layer(main, BACKGROUND);
        Render_Layer(main, WORLD);
        Render_Layer(main, PARTICLES);

        main.setTransform(getUITransform());
        Render_Layer(main, UI);
        Render_Layer(main, FOREGROUND);

        bs.show();
        main.dispose();
    }

    private AffineTransform getWorldTransform() {
        AffineTransform at = new AffineTransform();

        double ratio = Math.min(getHeight(), getWidth()) / (double) Math.max(getHeight(), getWidth());

        at.scale(1.0 / ratio, - 1.0 / ratio);
        at.translate(getWidth() * ratio, -getHeight() * ratio);

        return at;
    }

    private AffineTransform getUITransform() {
        AffineTransform at = new AffineTransform();

        return at;
    }

    private void Render_Layer(Graphics2D main, List<RenderLayer> renderStage) {
        for (RenderLayer renderLayer : renderStage) {
            renderLayer.Render(main);
        }
    }

    public void RegisterRenderLayer(RenderLayer renderLayer, RenderStage renderStage) {
        switch (renderStage) {
            case BACKGROUND -> BACKGROUND.add(renderLayer);
            case WORLD -> WORLD.add(renderLayer);
            case PARTICLES -> PARTICLES.add(renderLayer);
            case UI -> UI.add(renderLayer);
            case FOREGROUND -> FOREGROUND.add(renderLayer);
        }
    }
}
