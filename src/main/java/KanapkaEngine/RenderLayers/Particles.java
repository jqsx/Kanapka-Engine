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

        Point point = new Point((int) node.transform.getPosition().getX(), (int) node.transform.getPosition().getY());
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
        Vector2D camera_position = Camera.main.getPosition().scalarMultiply(-1);
        Dimension window_bounds = KanapkaEngine.Game.Window.getWindowSize();
        window_bounds = new Dimension(window_bounds.width * 2, window_bounds.height * 2);
        double g_size = SceneManager.getGlobalSize();
        cameraView.setBounds((int) (camera_position.getX() - window_bounds.width * 2 / g_size), (int) (camera_position.getY() - window_bounds.height * 2 / g_size), (int) (window_bounds.width * 4 / g_size), (int) (window_bounds.height * 4 / g_size));
    }

    private void renderParticle(Graphics2D main, Particle node, ParticleSystem<Particle> system) {
        BufferedImage render = system.getRender(node);
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Vector2D position = node.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = system.getParent().transform.getSize();
            size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
            boundingTextureBox.setBounds((int) -position.getX() - (int) (render.getWidth() * size.getX()), (int) -position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);

            if (cameraView.intersects(boundingTextureBox)) {
                Vector2D pos = new Vector2D((camera_position.getX() + position.getX() - system.getParent().transform.getSize().getX() / 2.0) / size.getX(), -(camera_position.getY() + position.getY() - system.getParent().transform.getSize().getY() / 2.0) / size.getY());
                AffineTransform at = new AffineTransform();
                double rad = Math.toRadians(system.getParent().transform.getRotation());
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate(pos.getX(), pos.getY());
                at.rotate(rad, render.getWidth() / 2.0, render.getHeight() / 2.0);
                main.drawImage(render, at, null);

                if (node instanceof Renderable renderable)
                    renderable.onRender(main, at);
                if (system instanceof Renderable renderable) {
                    renderable.onRender(main, at);
                }
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
