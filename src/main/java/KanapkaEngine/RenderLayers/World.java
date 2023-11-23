package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.Camera;
import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class World implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (Camera.main == null) {
            return;
        }
        for (Node node : SceneManager.getSceneNodes()) {
            renderNode(main, node);
        }
    }

    private void renderNode(Graphics2D main, Node node) {
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Dimension window_bounds = Window.getWindowSize();
            Vector2D position = node.transform.getPosition();
            Vector2D size = node.transform.getSize();
            Rectangle boundingTextureBox = new Rectangle((int) position.getX(), (int) position.getY(), (int) (render.getWidth() * size.getX()), (int) (render.getHeight() * size.getY()));
            Rectangle cameraView = new Rectangle((int) camera_position.getX() - window_bounds.width, (int) camera_position.getY() - window_bounds.height, window_bounds.width * 2, window_bounds.height * 2);

            if (cameraView.intersects(boundingTextureBox)) {
                AffineTransform at = new AffineTransform();
                double g_size = SceneManager.getGlobalSize();
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate((camera_position.getX() - position.getX()) / size.getX(), (camera_position.getY() - position.getY()) / size.getY());
                main.drawImage(render, at, null);
            }
        }
        renderChildNodes(main, node);
    }

    private void renderChildNodes(Graphics2D main, Node node) {
        for (int i = 0; i < node.childCount(); i++) {
            renderNode(main, node.getChild(i));
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
