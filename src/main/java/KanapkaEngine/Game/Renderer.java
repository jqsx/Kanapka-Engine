package KanapkaEngine.Game;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Engine;

import java.awt.*;
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

    public Renderer(EngineConfiguration engineConfiguration, Engine engine) {
        this.engineConfiguration = engineConfiguration;
        this.engine = engine;
    }

    public void Render() {
        if (engine.getRenderThreadID() != Thread.currentThread().getId()) return;
        if (render_frame_delay + Second / engineConfiguration.FPSLIMIT > System.nanoTime()) return;
        Frame();
    }

    private void Frame() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }

        Graphics2D main = (Graphics2D) bs.getDrawGraphics();

        Render_Layer(main, BACKGROUND);

        bs.show();
        main.dispose();
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
