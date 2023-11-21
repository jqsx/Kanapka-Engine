package KanapkaEngine.Game;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Engine;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends Canvas implements MouseListener {
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

    private boolean is_custom_titlebar = false;

    private Point start_mouse_move = new Point(0, 0);
    private Point last_mouse_position = new Point(0, 0);
    private boolean isMoving = false;

    public Renderer(EngineConfiguration engineConfiguration, Engine engine) {
        this.engineConfiguration = engineConfiguration;
        this.engine = engine;
        target_size = engineConfiguration.target_size;
        setBackground(Color.black);
        setSize(320, 180);
        is_custom_titlebar = engineConfiguration.custom_title_bar;
        if (is_custom_titlebar)
            addMouseListener(this);
    }

    public void Render() {
        if (is_custom_titlebar)
            update_window_position();

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

        AffineTransform world_transform = getWorldTransform();
        main.setTransform(world_transform);
        main.setColor(Color.red);
        main.fillRect(-500, -500, 1000, 1000);
        Render_Layer(main, BACKGROUND);
        main.setTransform(world_transform);
        Render_Layer(main, WORLD);
        main.setTransform(world_transform);
        Render_Layer(main, PARTICLES);

        AffineTransform ui_transform = getUITransform();
        main.setTransform(ui_transform);
        Render_Layer(main, UI);
        main.setTransform(ui_transform);
        Render_Layer(main, FOREGROUND);

        main.setTransform(new AffineTransform());
        if (is_custom_titlebar)
            render_custom_titlebar(main);

        bs.show();
        main.dispose();
    }

    private void render_custom_titlebar(Graphics2D main) {
        main.setColor(new Color(100, 100, 100, 100));

        main.fillRect(0, 0, getWidth() * 2, 30);

        main.setColor(Color.darkGray);
        main.fillRect(0, 0, 60, 30);
    }

    private void update_window_position() {
        if (isMoving) {
            Point component_screen_point = getLocationOnScreen();
            Point mouse_screen_position = getMousePosition();
            mouse_screen_position.translate(component_screen_point.x, component_screen_point.y);
            getParent().setLocation(new Point(mouse_screen_position.x - start_mouse_move.x, mouse_screen_position.y - start_mouse_move.y));
        }
    }

    private AffineTransform getWorldTransform() {
        AffineTransform at = new AffineTransform();
        Dimension target = Toolkit.getDefaultToolkit().getScreenSize();
        double ratio = Math.min(getHeight() / (double)target.height, getWidth() / (double)target.width) / (double) Math.max(getHeight() / (double)target.height, getWidth() / (double)target.width);

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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (new Rectangle(0, 0, getWidth() * 2, 30).contains(e.getPoint())) {
            isMoving = true;
            start_mouse_move = e.getPoint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMoving = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
