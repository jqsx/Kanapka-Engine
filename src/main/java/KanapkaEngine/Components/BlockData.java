package KanapkaEngine.Components;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Global data stored about a block that can then be accessed through an integer id.
 */
public class BlockData {
    /**
     * Do nodes with the rigidbody component collide with this block?
     */
    public boolean hasCollision = true;
    private BufferedImage render;
    private String texture;
    int render_stage = Renderer.NOT_STARTED;

    @Deprecated
    public boolean scale_render = true;
    public BlockData(String texture) {
        this.texture = texture;
        getRender();
    }
    public BlockData(BufferedImage image) {
        this.render = image;
    }
    public BlockData() {

    }

    /**
     * Used for perhaps unloading a block from the <code><stong>BlockManager</stong></code> (bad idea as all ids will shift) flushes the block render, and resets the block render state.
     */
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

    /**
     * Directly setting the visual image for the block.
     * @param image
     */
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
