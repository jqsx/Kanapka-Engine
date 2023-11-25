package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.awt.*;
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

    public final Point getPoint() {
        return new Point(point.x, point.y);
    }

    public BufferedImage getRender() {
        if (render_stage == Renderer.NOT_STARTED) beginRender();
        if (render_stage == Renderer.STARTED) return null;
        else return render;
    }

    private void beginRender() {

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
