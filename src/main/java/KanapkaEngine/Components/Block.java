package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Block class for the chunk system in place <br>
 * Not recommended to extend this class unless writing a custom data save and load rather than the one provided <br>
 * Contains one id for the block type and another for locating the special object.
 */
public class Block {
    public final Chunk parent;
    /**
     * Block position in the chunk, not world block position.
     */
    public final Point point;
    public int id = 0;
    public int special_id = 0;

    public int damage = 0;

    public Block(Chunk parent, Point point) {
        Objects.requireNonNull(point, "Missing chunk point.");
        Objects.requireNonNull(parent, "Missing parent.");
        this.point = point;
        this.parent = parent;
        parent.appendBlock(this);
    }

    public Block(Chunk parent, Point point, int id) {
        this(parent, point);
        if (id < 0)
            return;
        if (id >= BlockManager.getBlockCount())
            System.out.println("[WARN] Block id " + id + " is not registered in the BlockManager. Will default to block id 0 instead.");
        this.id = id;
    }

    /**
     * Returns the render image of the block.
     * @return
     */
    public final BufferedImage getRender() {
        return BlockManager.getBlockData(id).getRender();
    }

    public final BlockData getBlockData() {
        return BlockManager.getBlockData(id);
    }

    /**
     * Returns the world position of the block.
     * @return
     */
    public final Vector2D getPosition() {
        //double s = Chunk.BLOCK_SCALE / 2.0;
        Vector2D p = parent.getBlockPosition(point);
        return p;//.add(new Vector2D(-s * (p.getX() < 0 ? 1.0 : 0.0), 0));
    }

    public final Vector2D getCenter() {
        return getPosition().add(new Vector2D(Chunk.BLOCK_SCALE / 4.0, -Chunk.BLOCK_SCALE / 2.0));
    }
}
