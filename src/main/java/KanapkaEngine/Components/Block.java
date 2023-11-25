package KanapkaEngine.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Block {
    public final Chunk parent;
    public final Point point;

    public String texture = "";

    private BufferedImage render;
    private int render_stage = 0;

    public Block(Chunk parent, Point point) {
        Objects.requireNonNull(point, "Missing chunk point.");
        Objects.requireNonNull(parent, "Missing parent.");
        this.point = point;
        this.parent = parent;
    }

    public final BufferedImage getRender() {
        if (render_stage == Renderer.NOT_STARTED) {
            beginRender();
            render_stage = Renderer.STARTED;
        }
        if (render_stage == Renderer.STARTED) return null;
        else return render;
    }

    void beginRender() {

    }

    void finishedRender() {
        render_stage = Renderer.FINISHED;
    }

    public final void append() {
        parent.appendBlock(this);
    }
}
