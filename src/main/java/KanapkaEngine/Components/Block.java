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

    public Block(Chunk parent, Point point) {
        Objects.requireNonNull(point, "Missing chunk point.");
        Objects.requireNonNull(parent, "Missing parent.");
        this.point = new Point(point.x, point.y);
        this.parent = parent;
        parent.appendBlock(this);
    }

    public Block(Chunk parent, Point point, int id) {
        this(parent, point);
        if (id < 0)
            return;
        if (id >= BlockManager.getBlockCount())
            logger.warn("Block id " + id + " is not registered in the BlockManager. Will default to block id 0 instead.");
        this.id = id;
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
            Block block = new Block(chunk);

            _buffer.clear();
            _buffer.put(data);

            block.point.x = _buffer.get();
            block.point.y = _buffer.get();

            block.id = _buffer.getInt();
            if (block.id < 0)
                return null;
            if (block.id >= BlockManager.getBlockCount()) {
                logger.warn("Problem while deserializing world chunk data: Block id " + block.id + " is not registered in the BlockManager. Will default to block id 0 instead.");
                block.id = 0;
            }
            block.special_id = _buffer.getInt();

            chunk.appendBlock(block);

            return block;
        }
    }

    public String toString() {

        BlockData data = getBlockData();

        return "BLOCK:" +" -f:" +data.isFloor() + " -hc:" +data.hasCollision() + " -id:" + data.getID();
    }
}
