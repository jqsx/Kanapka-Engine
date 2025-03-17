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
    private boolean hasCollision = true;
    private boolean floor = false;
    private BufferedImage render;

    private int block_id = -1;

    public int blockStrength() {
        return blockStrength;
    }

    public int block_id() {
        return block_id;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public boolean isFloor() {
        return floor;
    }

    private int blockStrength = 1;

    public BlockData(Builder builder) {
        this.render = builder.render;
        this.hasCollision = builder.hasCollision;
        this.floor = builder.isFloor;
        this.blockStrength = builder.blockStrength;
    }

    protected void setBlockID(int id) {
        this.block_id = id;
    }

    public final boolean isRegistered() {
        return block_id >= 0;
    }

    public final int getID() {
        return block_id;
    }

    /**
     * Future plans: Create a way to render dynamic textures and make it possible to define depth blocks filtering any objects that are behind them and making them visible
     *
     *
     * @return
     */
    public final BufferedImage getRender() {
        return render;
    }

    /**
     * Directly setting the visual image for the block.
     * @param image
     */
    public final void setImage(BufferedImage image) {
        Objects.requireNonNull(image);
        this.render = image;
        /*        this.render_stage = Renderer.FINISHED;*/
    }

    public static class Builder {
        public boolean hasCollision() {
            return hasCollision;
        }

        public Builder setCollision(boolean hasCollision) {
            this.hasCollision = hasCollision;
            return this;
        }

        public boolean isFloor() {
            return isFloor;
        }

        public Builder setFloor(boolean floor) {
            isFloor = floor;
            return this;
        }

        public BufferedImage render() {
            return render;
        }

        public Builder setRender(BufferedImage render) {
            this.render = render;
            return this;
        }

        public int blockStrength() {
            return blockStrength;
        }

        public Builder setBlockStrength(int blockStrength) {
            this.blockStrength = blockStrength;
            return this;
        }

        private boolean hasCollision = true;
        private boolean isFloor = false;
        private BufferedImage render;
        private int blockStrength = 1;
    }
}
