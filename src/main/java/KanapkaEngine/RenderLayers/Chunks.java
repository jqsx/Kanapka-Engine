package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Chunks implements RenderLayer {
    public static int VisibleChunks = 0;

    public static double DEACTIVATIONDELAY = 5.0;

    private static final LinkedList<Chunk> activeChunks = new LinkedList<>();
    private Thread chunkUpdateThread;

    public static Iterator<Chunk> getActiveChunks() {
        return activeChunks.iterator();
    }

    public Chunks() {
        chunkUpdateThread = new Thread(this::UpdateChunks);
        chunkUpdateThread.start();
    }

    @Override
    public void Render(Graphics2D main) {
        VisibleChunks = 0;
        if (Camera.main == null) return;
        if (!SceneManager.hasScene()) return;
        int s = SceneManager.getCurrentlyLoaded().getChunkSize() * Chunk.BLOCK_SCALE;
        double g_size = SceneManager.getGlobalSize();
        Vector2D position = Camera.main.getPosition();
        Point cameraOffset = new Point((int) Math.floor((position.getX() - (position.getX() % s)) / s), (int) Math.floor((position.getY() - (position.getY() % s)) / s));
        Dimension window_bounds = Window.getWindowSize();
        Rectangle2D camView = new Rectangle2D.Double(position.getX() - (window_bounds.width / g_size),
                position.getY() - (window_bounds.height / g_size),
                window_bounds.width / g_size * 2.0,
                window_bounds.height / g_size * 2.0
        );

        int dist = 1 + (int)Math.floor(5.0 / g_size);
        int dist_w = (int)Math.round(dist * ((double)window_bounds.width / (double)window_bounds.height));

        for (int i = -dist_w; i <= dist_w; i++) {
            for (int j = -dist; j <= dist; j++) {
                Chunk c = SceneManager.getCurrentlyLoaded().scene_world.get(i - cameraOffset.x, j - cameraOffset.y);
                if (c != null) {
                    c.activate();
                    if (!activeChunks.contains(c)) activeChunks.add(c);
                    renderChunk(main, c, camView);
                }
            }
        }
    }

    private void UpdateChunks() {
        long last_run = System.currentTimeMillis();
        System.out.println("Chunk deactivation thread active.");
        while (true) {
            if (last_run + 1000L / 30L < System.currentTimeMillis()) {

                try {
                    IterateChunks();
                } catch (ConcurrentModificationException ignore) {

                }

                last_run = System.currentTimeMillis();
            }
        }
    }

    private void IterateChunks() {
        activeChunks.removeIf(chunk -> {
            chunk.CheckDeactivation();
            return !chunk.IsActive();
        });
    }

    private void oldrenderChunk(Graphics2D main, Chunk chunk) {
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
                VisibleChunks++;
            }
        }
    }

    private void renderChunk(Graphics2D main, Chunk chunk, Rectangle2D camView) {
        BufferedImage render = chunk.getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Vector2D position = chunk.getPosition();
            Vector2D pos = new Vector2D((camera_position.getX() + position.getX()), -(camera_position.getY() + position.getY()));
            double g_size = SceneManager.getGlobalSize();

            Rectangle2D chunkBound = chunk.getBounds();

            if (true) { //camView.intersects(chunkBound)) {
                AffineTransform at = new AffineTransform();
                at.scale(g_size, g_size);
                at.translate(pos.getX(), pos.getY());

                main.drawImage(render, at, null);
                VisibleChunks++;
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.BACKGROUND;
    }
}
