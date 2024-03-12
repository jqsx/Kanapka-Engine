package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ChunkNodes implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (Camera.main == null) return;
        if (!SceneManager.hasScene()) return;
        int s = SceneManager.getCurrentlyLoaded().getChunkSize() * Chunk.BLOCK_SCALE;
        double g_size = SceneManager.getGlobalSize();
        Vector2D position = Camera.main.getPosition();
        Point cameraOffset = new Point((int) Math.floor((position.getX() - (position.getX() % s)) / s), (int) Math.floor((position.getY() - (position.getY() % s)) / s));
        Dimension window_bounds = Window.getWindowSize();

        int dist = 1 + (int)Math.floor(5.0 / g_size);
        int dist_w = (int)Math.round(dist * ((double)window_bounds.width / (double)window_bounds.height));

        for (int i = -dist_w; i <= dist_w; i++) {
            for (int j = -dist; j <= dist; j++) {
                Chunk c = SceneManager.getCurrentlyLoaded().scene_world.get(i - cameraOffset.x, j - cameraOffset.y);
                if (c != null && c.IsActive()) {
                    c.getChunkNodes().forEachRemaining(node -> renderNode(main, node));
                }
            }
        }
    }

    private void renderNode(Graphics2D main, Node node) {
        if (node.getRenderer() == null) {
            renderChildNodes(main, node);
            return;
        }
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Dimension window_bounds = Window.getWindowSize();
            if (!Engine.isMacOS()) window_bounds.setSize(window_bounds.width / 2.0, window_bounds.height / 2.0);
            Vector2D position = node.transform.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = node.transform.getSize();
            size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
            Rectangle boundingTextureBox = new Rectangle((int) -position.getX() - (int) (render.getWidth() * size.getX()), (int) -position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);
            Rectangle cameraView = new Rectangle((int) (camera_position.getX() - window_bounds.width / g_size), (int) (camera_position.getY() - window_bounds.height / g_size), (int) (window_bounds.width * 2 / g_size), (int) (window_bounds.height * 2 / g_size));
            if (true){//cameraView.intersects(boundingTextureBox)) {
                Vector2D pos = new Vector2D((camera_position.getX() + position.getX() - node.transform.getSize().getX() / 2.0) / size.getX(), -(camera_position.getY() + position.getY() - node.transform.getSize().getY() / 2.0) / size.getY());
                AffineTransform at = new AffineTransform();
                double rad = Math.toRadians(node.transform.getRotation());
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate(pos.getX(), pos.getY());
                at.rotate(rad, render.getWidth() / 2.0, render.getHeight() / 2.0);
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
        return RenderStage.BACKGROUND;
    }
}
