package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Chunks implements RenderLayer {

    @Override
    public void Render(Graphics2D main) {
        if (Camera.main == null) return;
        if (!SceneManager.hasScene()) return;
        int s = SceneManager.getCurrentlyLoaded().getChunkSize() * Chunk.BLOCK_SCALE;
        Vector2D position = Camera.main.getPosition();
        Point cameraOffset = new Point((int) Math.floor((position.getX() - (position.getX() % s)) / s), (int) Math.floor((position.getY() - (position.getY() % s)) / s));
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Chunk test = SceneManager.getCurrentlyLoaded().scene_world.get(i + cameraOffset.x, j + cameraOffset.y);
                if (test != null)
                    renderChunk(main, test);
            }
        }
    }

    private void renderChunk(Graphics2D main, Chunk chunk) {
        BufferedImage render = chunk.getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Dimension window_bounds = Window.getWindowSize();
            if (!Engine.isMacOS()) window_bounds.setSize(window_bounds.width / 2.0, window_bounds.height / 2.0);
            Vector2D position = chunk.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = new Vector2D(1,1);
            Rectangle boundingTextureBox = new Rectangle((int) position.getX() - (int) (render.getWidth() * size.getX()), (int) position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);
            Rectangle cameraView = new Rectangle((int) (camera_position.getX() - window_bounds.width / g_size), (int) (camera_position.getY() - window_bounds.height / g_size), (int) (window_bounds.width * 2 / g_size), (int) (window_bounds.height * 2 / g_size));

            if (cameraView.intersects(boundingTextureBox)) {
                AffineTransform at = new AffineTransform();
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate((camera_position.getX() - position.getX()) / size.getX(), (camera_position.getY() - position.getY()) / size.getY());
                main.drawImage(render, at, null);
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.BACKGROUND;
    }
}
