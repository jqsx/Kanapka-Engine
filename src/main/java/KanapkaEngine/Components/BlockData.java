package KanapkaEngine.Components;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class BlockData {
    public boolean hasCollision = true;
    private BufferedImage render;
    private String texture;
    int render_stage = Renderer.NOT_STARTED;
    public boolean scale_render = true;
    public BlockData(String texture) {
        this.texture = texture;
        getRender();
    }
    public BlockData() {

    }

    public final void Derender() {
        render.flush();
        render = null;
        render_stage = Renderer.NOT_STARTED;
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

    private void beginRender() {
        if (texture == null) return;
        render = ResourceLoader.loadResource(texture);
        render_stage = Renderer.FINISHED;
    }
}
