package KanapkaEngine.Components;

import KanapkaEngine.Game.Chunk;
import KanapkaEngine.Game.Logger;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Block class for the chunk system in place <br>
 * Not recommended to extend this class unless writing a custom data save and load rather than the one provided <br>
 * Contains one id for the block type and another for locating the special object.
 */
public class Block {
    private static final Logger logger = new Logger("BLOCK");
    private static final ByteBuffer _buffer = ByteBuffer.allocate(10);

    public final Chunk parent;
    /**
     * Block position in the chunk, not world block position.
     */
    public final Point point;
    public int id = 0;
    public int special_id = 0;

    public int damage = 0;

    public Block(Chunk parent, Point point, int id) {
        Objects.requireNonNull(point, "Missing chunk point.");
        Objects.requireNonNull(parent, "Missing parent.");
        this.point = new Point(point.x, point.y);
        this.parent = parent;
        if (id < 0) {
            throw new RuntimeException("Invalid block id.");
//            return;
        }
        else if (id >= BlockManager.getBlockCount()) {
//            logger.warn("Block id " + id + " is not registered in the BlockManager. Will default to block id 0 instead.");
            throw new RuntimeException("Invalid block id.");
        }
        this.id = id;
        parent.appendBlock(this);
    }

    private Block(Chunk parent) {
        this.parent = parent;

        point = new Point();
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
    public final Vector2d getPosition() {
        //double s = Chunk.BLOCK_SCALE / 2.0;
        Vector2d p = parent.getBlockPosition(point);
        return p;//.add(new Vector2d(-s * (p.getX() < 0 ? 1.0 : 0.0), 0));
    }

    public final Vector2d getCenter() {
        return getPosition().add(new Vector2d(Chunk.BLOCK_SCALE / 4.0, -Chunk.BLOCK_SCALE / 2.0));
    }

    public static class BlockSerializer {
        private final Point point = new Point(0, 0);

        public byte[] SerializationData(Block block) {
            _buffer.clear();

            byte x = (byte) Mathd.Clamp(block.point.x, 0, 127);
            byte y = (byte) Mathd.Clamp(block.point.y, 0, 127);
            _buffer.put(x);
            _buffer.put(y);
            _buffer.putInt(block.id);
            _buffer.putInt(block.special_id);

            return _buffer.array();
        }

        public int SerializationDataSize() {
            return _buffer.capacity();
        }

        public Block Deserialize(Chunk chunk, byte[] data) {
            _buffer.clear();

            byte x = _buffer.get();
            byte y = _buffer.get();

            int id = _buffer.getInt();

            int special = _buffer.getInt();

            point.setLocation(x,y);

            Block block = chunk.createBlock(id, point);

            if (block == null)
                return null;
            block.special_id = special;

            return block;
        }
    }

    public String toString() {

        BlockData data = getBlockData();

        return "BLOCK:" +" -f:" +data.isFloor() + " -hc:" +data.hasCollision() + " -id:" + data.getID();
    }
}
