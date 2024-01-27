package KanapkaEngine.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Block {
    public final Chunk parent;
    public final Point point;

    public String texture;

    private BufferedImage render;
    private int render_stage = 0;
    public boolean scale_render = true;

    public Block(Chunk parent, Point point) {
        Objects.requireNonNull(point, "Missing chunk point.");
        Objects.requireNonNull(parent, "Missing parent.");
        this.point = point;
        this.parent = parent;
    }

    public final BufferedImage getRender() {
        if (render_stage == Renderer.NOT_STARTED) {
            render_stage = Renderer.STARTED;
            beginRender();
        }
        return render;
    }

    public final void setImage(BufferedImage image) {
        Objects.requireNonNull(image);
        this.render = image;
        this.render_stage = Renderer.FINISHED;
    }

    void beginRender() {
        if (texture == null) return;
        render = ResourceLoader.loadResource(texture);
        render_stage = Renderer.FINISHED;
    }

    public final void append() {
        parent.appendBlock(this);
    }

    public final void Derender() {
        render.flush();
        render = null;
        render_stage = Renderer.NOT_STARTED;
    }
}
