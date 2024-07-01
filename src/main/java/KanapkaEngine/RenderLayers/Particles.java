package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class Particles implements RenderLayer {

    private final Rectangle cameraView = new Rectangle();
    private final Rectangle boundingTextureBox = new Rectangle();
    @Override
    public void Render(Graphics2D main) {
        recalculateCameraView();
        try {
            SceneManager.getSceneNodes().foreach(node -> {
                renderParticleSystem(main, node);
                World.Visible_Nodes++;
            });
        } catch (ConcurrentModificationException | NullPointerException | ArrayIndexOutOfBoundsException ignore) {

        }
    }

    private void renderParticleSystem(Graphics2D main, Node node) {
        Renderer renderer = node.getRenderer();

        Point point = Camera.main.WorldToScreenPosition(node.transform.getPosition());
        if (cameraView.contains(point)) {
            if (renderer instanceof ParticleSystem particleSystem) {
                try {
                    particleSystem.getList().foreach(particle -> {
                        renderParticle(main, (Particle) particle, particleSystem);
                    });

                } catch (ConcurrentModificationException | NullPointerException | ArrayIndexOutOfBoundsException ignore) {

                }
            }
        }

        for (int i = 0; i < node.childCount(); i++) {
            renderParticleSystem(main, node.getChild(i));
        }
    }

    private void recalculateCameraView() {
        if (Camera.main == null) return;
        Vector2D camera_position = Camera.main.getPosition();
        Dimension window_bounds = KanapkaEngine.Game.Window.getWindowSize();
        window_bounds = new Dimension(window_bounds.width * 2, window_bounds.height * 2);
        double g_size = SceneManager.getGlobalSize();
        cameraView.setBounds((int) (-window_bounds.width / 2.0), (int) (-window_bounds.height / 2.0), window_bounds.width, window_bounds.height);
        //cameraView.setBounds((int) (camera_position.getX() - window_bounds.width * 2 / g_size), (int) (camera_position.getY() - window_bounds.height * 2 / g_size), (int) (window_bounds.width * 4 / g_size), (int) (window_bounds.height * 4 / g_size));
    }

    private void renderParticle(Graphics2D main, Particle node, ParticleSystem<Particle> system) {
        BufferedImage render = system.getRender(node);
        if (render != null) {

            AffineTransform at = getTransform(node, system);

            boundingTextureBox.setBounds((int) at.getTranslateX(), (int) at.getTranslateY(), (int) (render.getWidth() * at.getScaleX()), (int) (render.getHeight() * at.getScaleY()));

            if (cameraView.intersects(boundingTextureBox)) {
                main.drawImage(render, at, null);

                {
                    Vector2D screenPosition = new Vector2D(at.getTranslateX(), at.getTranslateY());
                    Vector2D _size = new Vector2D(render.getWidth() * at.getScaleX(), render.getHeight() * at.getScaleY());
                    if (node instanceof Renderable renderable) {
                        renderable.onRender(main, screenPosition, _size);
                    }
                    if (system instanceof Renderable renderable) {
                        renderable.onRender(main, screenPosition, _size);
                    }
                }
            }
        }
    }

    private AffineTransform getTransform(Particle node, ParticleSystem<Particle> system) {
        AffineTransform at = new AffineTransform();

        BufferedImage render = system.getRender(node);

        double gSize = SceneManager.getGlobalSize();

        Vector2D size = system.getParent().transform.getSize();
        size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
        Vector2D cameraPosition = Camera.main.getPosition();
        Vector2D position = cameraPosition.add(node.getPosition());
        position = position.add(new Vector2D(0, system.getParent().transform.getSize().getY() / 2.0));

        //position = new Vector2D(gSize / position.getX(), gSize / position.getY());

        at.scale(size.getX() * gSize, size.getY() * gSize);
        at.translate(position.getX() / size.getX(), -position.getY() / size.getY());

        return at;
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
