package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Chunk {
    public static final int BLOCK_SCALE = 64;
    private BufferedImage render;
    private int render_stage = 0;
    private final World parent;
    private final Point point;
    private final Block[][] blocks;

    public final void appendBlock(Block block) {
        if (block.parent == this) {
            blocks[block.point.x][block.point.y] = block;
        }
    }

    private boolean isInRange(Point p) {
        return isInRange(p.x) && isInRange(p.y);
    }

    private boolean isInRange(int a) {
        return a <= 9 && a >= 0;
    }

    private Chunk(Point point, World parent) {
        this.point = point;
        this.parent = parent;
        if (SceneManager.hasScene())
            blocks = new Block[SceneManager.getCurrentlyLoaded().getChunkSize()][SceneManager.getCurrentlyLoaded().getChunkSize()];
        else
            throw new RuntimeException("No scene loaded.");
    }

    public Vector2D getPosition() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2D(point.x * s, point.y * s);
    }

    public static Vector2D getSize() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2D(s, s);
    }

    public final Point getPoint() {
        return new Point(point.x, point.y);
    }

    public BufferedImage getRender() {
        if (render_stage == Renderer.NOT_STARTED) beginRender();
        if (render_stage == Renderer.STARTED) return render;
        else return render;
    }

    private void beginRender() {
        new Thread(() -> {
            int s = SceneManager.getCurrentlyLoaded().getChunkSize() * BLOCK_SCALE;
            BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            for (Block[] column : blocks)
                for (Block block : column) {
                    if (block == null) continue;
                    BufferedImage block_render = block.getRender();
                    AffineTransform at = new AffineTransform();
                    if (block.scale_render) {
                        at.translate(block.point.x * BLOCK_SCALE, block.point.y * BLOCK_SCALE);
                        at.scale(BLOCK_SCALE / (double)block_render.getWidth(), -BLOCK_SCALE / (double)block_render.getHeight());
                        at.translate(0, -BLOCK_SCALE);
                    }
                    else {
                        Vector2D m = new Vector2D(BLOCK_SCALE / 2.0 - block_render.getWidth() / 2.0, BLOCK_SCALE - block_render.getHeight() / 2.0);
                        at.translate(block.point.x * BLOCK_SCALE + m.getX(), block.point.y * BLOCK_SCALE + m.getY());
                        at.scale(1, -1);
                    }

                    g.drawImage(block_render, at, null);
                }

            g.dispose();
            render = image;

            render_stage = Renderer.FINISHED;
        }).start();
    }

    private void scaleAt(AffineTransform at, BufferedImage image) {
        at.translate(image.getWidth() / 2.0, image.getHeight() / 2.0);
        at.scale(1, -1);
        at.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
    }

    private void finishedRender() {
        render_stage = Renderer.FINISHED;
    }


    public static Chunk build(Point point, World parent) {
        return new Chunk(Objects.requireNonNull(point, "Missing chunk point."), Objects.requireNonNull(parent, "Missing parent."));
    }

    public World getParent() {
        return parent;
    }
}
