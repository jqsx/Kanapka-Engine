package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class Particles implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        for (Node node : SceneManager.getSceneNodes()) {
            Renderer renderer = node.getRenderer();
            if (renderer instanceof ParticleSystem) {
                ParticleSystem particleSystem = (ParticleSystem) renderer;
                try {
                    LinkedList<Particle> temp_particles = new LinkedList<Particle>(particleSystem.getList());
                    temp_particles.forEach(particle -> renderParticle(main, particle, particleSystem));
                    temp_particles.clear();
                } catch (ConcurrentModificationException | NullPointerException e) {

                }
            }
        }
    }

    private void renderParticle(Graphics2D main, Particle node, ParticleSystem<Particle> system) {
        BufferedImage render = system.getRender(node);
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Dimension window_bounds = KanapkaEngine.Game.Window.getWindowSize();
            if (!Engine.isMacOS()) window_bounds.setSize(window_bounds.width / 2.0, window_bounds.height / 2.0);
            Vector2D position = node.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = system.getParent().transform.getSize();
            size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
            Rectangle boundingTextureBox = new Rectangle((int) -position.getX() - (int) (render.getWidth() * size.getX()), (int) -position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);
            Rectangle cameraView = new Rectangle((int) (camera_position.getX() - window_bounds.width / g_size), (int) (camera_position.getY() - window_bounds.height / g_size), (int) (window_bounds.width * 2 / g_size), (int) (window_bounds.height * 2 / g_size));
            if (cameraView.intersects(boundingTextureBox)) {
                Vector2D pos = new Vector2D((camera_position.getX() + position.getX() - system.getParent().transform.getSize().getX() / 2.0) / size.getX(), -(camera_position.getY() + position.getY() - system.getParent().transform.getSize().getY() / 2.0) / size.getY());
                AffineTransform at = new AffineTransform();
                double rad = Math.toRadians(system.getParent().transform.getRotation());
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate(pos.getX(), pos.getY());
                at.rotate(rad, render.getWidth() / 2.0, render.getHeight() / 2.0);
                main.drawImage(render, at, null);
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
