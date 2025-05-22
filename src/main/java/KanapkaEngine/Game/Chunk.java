package KanapkaEngine.Game;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import org.apache.logging.log4j.LogManager;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

public class Chunk {
    /**
     * Hardcoded block scale
     */

    private static final Logger logger = new Logger("CHUNK");

    private static final Vector<Chunk> activeChunks = new Vector<>();

    public static final int BLOCK_SCALE = 16;
    private Rectangle2D bounds;
    protected Texture renderTexture;
    private BufferedImage render;
    private Renderer.Stage render_stage = Renderer.Stage.NOTSTARTED;
    private final World parent;
    private final Point point;
    private final Block[][] blocks;
    private final Block[][] floor;

    public boolean isReady() {
        return isReadyForRender;
    }

    private boolean isReadyForRender = false;
    private boolean isActive = false;
    private boolean needReRender = false;
    private long lastActive = System.currentTimeMillis();

    /**
     * Set to your own block serializer in order to parse blocks correctly
     */
    public static Block.BlockSerializer blockSerializer = new Block.BlockSerializer();
    public static ChunkSerializer chunkSerializer = new ChunkSerializer();

    /**
     * Internally managed function for appending blocks <br>
     * Will not append if block isn't parented tho this chunk.
     * @param block
     */
    public final void appendBlock(Block block) {
        Objects.requireNonNull(block);
        if (!isInRange(block.point)) return;
        Block old = blocks[block.point.x][block.point.y];
        Block old_floor = floor[block.point.x][block.point.y];
        if (block.parent == this && isInRange(block.point)) {
            Block target = old;

            BlockData data = block.getBlockData();

            if (!data.isRegistered())
                return;

            boolean _IsBlockFloor = data.isFloor();

            if (_IsBlockFloor) {
                floor[block.point.x][block.point.y] = block;
                target = old_floor;
            }
            else {
                 blocks[block.point.x][block.point.y] = block;
            }
            if (target == null) needReRender = true;
            else if (target.id != block.id) needReRender = true;
        }
    }

    /**
     * Setting the block at point to air.
     * @param p
     */
    public final void setAir(Point p) {
        if (isInRange(p)) {
            Block old = blocks[p.x][p.y];
            Block old_floor = floor[p.x][p.y];
            if (old != null || old_floor != null) {
                blocks[p.x][p.y] = null;
                floor[p.x][p.y] = null;
                needReRender = true;
            }
        }
    }

    /**
     * Block creation function that allows for easy block creation for the chunk.
     * @param id (block id) if negative -> set to air/null
     * @param p (local block)
     * @return Created block if id is more than 0
     */
    public final Block createBlock(int id, Point p) {
        if (id < 0) {
            setAir(p);
            return null;
        }
        return instantiateBlockObject(id, p);
    }

    /**
     * Access point to instantiate your own Block class extension.
     * @param id
     * @param p
     * @return
     */
    public Block instantiateBlockObject(int id, Point p) {
        return new Block(this, p, id);
    }

    /**
     * Internal chunk bounds method.
     * @return The bounds of the chunk relative to the camera view.
     */
    public Rectangle2D getBounds() {
        Vector2d camera_position = Camera.main.getPosition();
        Vector2d position = getPosition();
        Vector2d pos = new Vector2d((camera_position.x + position.x), -(camera_position.y + position.y));
        if (bounds == null)
            bounds = new Rectangle2D.Double(pos.x, pos.y, render.getWidth(), render.getHeight());
        else
            bounds.setFrame(pos.x, pos.y, render.getWidth(), render.getHeight());
        return bounds;
    }

    public Block getBlock(int x, int y) {
        return getBlock(new Point(x, y));
    }

    public Block getBlock(Point p) {
        if (isInRange(p)) {
            return blocks[p.x][p.y];
        }
        return null;
    }

    private boolean isInRange(Point p) {
        return isInRange(p.x) && isInRange(p.y);
    }

    private boolean isInRange(int a) {
        return a < SceneManager.getCurrentlyLoaded().getChunkSize() && a >= 0;
    }

    private Chunk(Point point, World parent) {
        this.point = point;
        this.parent = parent;
        if (SceneManager.hasScene()) {
            blocks = new Block[SceneManager.getCurrentlyLoaded().getChunkSize()][SceneManager.getCurrentlyLoaded().getChunkSize()];
            floor = new Block[SceneManager.getCurrentlyLoaded().getChunkSize()][SceneManager.getCurrentlyLoaded().getChunkSize()];
        }
        else
            throw new RuntimeException("No scene loaded.");
        parent.set(this);
    }

    /**
     * Returns world position of the chunk
     * @return
     */
    public Vector2d getPosition() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2d(point.x * s, point.y * s);
    }

    /**
     *
     * @param p
     * @return World position of block at <strong>p</strong>
     */
    public Vector2d getBlockPosition(Point p) {
        return getPosition().add(new Vector2d(p.x, -p.y).sub(new Vector2d(BLOCK_SCALE, BLOCK_SCALE)));
    }

    /**
     * Returns the size of the chunk in world scale
     * @return
     */
    public static Vector2d getSize() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2d(s, s);
    }

    public final Point getPoint() {
        return new Point(point.x, point.y);
    }

    public BufferedImage getRender() {
//        if (!isActive) return null;
        if (render_stage == Renderer.Stage.NOTSTARTED || needReRender) beginRender();
        if (render_stage == Renderer.Stage.FINISHED || render_stage == Renderer.Stage.READYTOBIND || render_stage == Renderer.Stage.BOUND) return render;
        else return null;
    }

    public final ImmutableBlocks getBlocks() {
        return new ImmutableBlocks(blocks);
    }

    public final ImmutableBlocks getFloor() {
        return new ImmutableBlocks(floor);
    }

    public Texture getTexture() {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }
        getRender();

        if (render_stage == Renderer.Stage.READYTOBIND) {
            if (renderTexture == null)
                renderTexture = new Texture();
            renderTexture.setTexture(render);
            render_stage = Renderer.Stage.BOUND;
        }

        return renderTexture;
    }

    public final void activate() {
        isActive = true;
        lastActive = System.currentTimeMillis();

        if (!activeChunks.contains(this))
            activeChunks.add(this);
    }

    private void deactivate() {
        if (isActive && render != null)
            if (lastActive + 50L < System.currentTimeMillis()) {
                render_stage = Renderer.Stage.NOTSTARTED;
                isActive = false;
                render.flush();
                render = null;
                bounds = null;
                renderTexture.Dispose();
                renderTexture = null;
            }
    }

    public final boolean IsActive() {
        return isActive;
    }

    private void beginRender() {
        if (!isReadyForRender && !needReRender) return;
        if (needReRender)
            needReRender = false;

        if (renderTexture == null || renderTexture.isDisposed()) {
            renderTexture = new Texture();
        }

        render_stage = Renderer.Stage.RENDERING;

        new Thread(() -> {
            int s = SceneManager.getCurrentlyLoaded().getChunkSize() * BLOCK_SCALE;
            BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            for (int x = 0; x < floor.length; x++) {
                for (int y = 0; y < floor.length; y++) {
                    Block floorBlock = floor[x][y];
                    Block blockBlock = blocks[x][y];

                    if (floorBlock != null) {
                        BufferedImage block_render = floorBlock.getRender();
                        AffineTransform at = getAffineTransform(floorBlock, block_render);
                        g.drawImage(block_render, at, null);
                    }
                    if (blockBlock != null) {
                        BufferedImage block_render = blockBlock.getRender();
                        AffineTransform at = getAffineTransform(blockBlock, block_render);
                        g.drawImage(block_render, at, null);
                    }
                }
            }

            render = image;
            g.dispose();
            render_stage = Renderer.Stage.READYTOBIND;
        }).start();
    }

    /**
     * <strong style="color: red;">!!! IMPORTANT !!!</strong>
     * <br><br> Needs to be called before the render thread is allowed to render the object. <br><br>
     * <strong style="color: red;">!!! IMPORTANT !!!</strong>
     */
    public final void ready() {
        isReadyForRender = true;
    }

    private AffineTransform getAffineTransform(Block block, BufferedImage block_render) {
        if (block_render == null)
            return new AffineTransform();
        AffineTransform at = new AffineTransform();
        Vector2d p = new Vector2d(block.point.x * BLOCK_SCALE, block.point.y * BLOCK_SCALE);
        at.scale(BLOCK_SCALE / (double) block_render.getWidth(), BLOCK_SCALE / (double) block_render.getHeight());
        at.translate(p.x * ((double) block_render.getWidth() / BLOCK_SCALE), p.y * ((double) block_render.getHeight() / BLOCK_SCALE));
        return at;
    }

    private void finishedRender() {
//        render_stage = Renderer.FINISHED;
    }

    /**
     * Chunk builder function
     * @param point in world <strong style="color: rgb(255, 200, 30);">parent</strong>
     * @param parent
     * @return Generated chunk
     */
    public static Chunk build(Point point, World parent) {
        return new Chunk(Objects.requireNonNull(point, "Missing chunk point."), Objects.requireNonNull(parent, "Missing parent."));
    }

    public World getParent() {
        return parent;
    }

    /**
     * Internal Update Call
     */
    protected static void UpdateChunks() {
        try {
            activeChunks.forEach(Chunk::deactivate);
            activeChunks.removeIf(chunk -> !chunk.IsActive());
        } catch (ConcurrentModificationException ignore) {

        }
    }

    /**
     * Im not spoon feeding you here I had to write this reader and now its your turn to live the nightmare
     * <br><br>
     * Now how does this save the block data in the first place: the blocks have their point, id, and special id buffered. The point object is buffered as 2 bytes in order to save on bytes therefore limiting chunk size to 127x127 which they probably shouldn't be as it defeats the point of the system in the first place. If the block value is null then its not buffered and its skipped.
     */
    public static class ChunkSerializer {
        private static final ByteBuffer _buffer = ByteBuffer.allocate(Integer.BYTES * 4);

        private int block_count = 0;
        private int floor_count = 0;

        public byte[] Serialize(Chunk chunk) {
            if (chunk == null)
                return new byte[0];

            ImmutableBlocks temp_blocks = chunk.getBlocks();
            ImmutableBlocks floor_blocks = chunk.getFloor();

            CalculateBlockCount(temp_blocks);
            CalculateFloorCount(floor_blocks);

            logger.log("Block Count: " + block_count);
            logger.log("Floor Count: " + floor_count);

            int b_size = blockSerializer.SerializationDataSize();

            ByteBuffer buffer = ByteBuffer.allocate(ChunkDataSize() + block_count * b_size + floor_count * b_size);

            buffer.put(ChunkData(chunk));
            for (int x = 0; x < temp_blocks.length; x++) {
                for (int y = 0; y < temp_blocks.length; y++) {
                    Block value = temp_blocks.get(x,y);
                    if (value == null) continue;
                    buffer.put(blockSerializer.SerializationData(value));
                }
            }

            for (int x = 0; x < floor_blocks.length; x++) {
                for (int y = 0; y < floor_blocks.length; y++) {
                    Block value = floor_blocks.get(x,y);
                    if (value == null) continue;
                    buffer.put(blockSerializer.SerializationData(value));
                }
            }

            return buffer.array();
        }

        public byte[] ChunkData(Chunk chunk) {
            _buffer.clear();

            _buffer.putInt(chunk.point.x);
            _buffer.putInt(chunk.point.y);
            _buffer.putInt(block_count);
            _buffer.putInt(floor_count);

            return _buffer.array();
        }

        private void CalculateBlockCount(ImmutableBlocks temp_blocks) {
            int blockcount = 0;
            for (int x = 0; x < temp_blocks.length; x++) {
                for (int y = 0; y < temp_blocks.length; y++) {
                    if (temp_blocks.get(x, y) != null)
                        blockcount++;
                }
            }

            block_count = blockcount;
        }

        private void CalculateFloorCount(ImmutableBlocks floor_blocks) {
            int floorcount = 0;
            for (int x = 0; x < floor_blocks.length; x++) {
                for (int y = 0; y < floor_blocks.length; y++) {
                    if (floor_blocks.get(x, y) != null)
                        floorcount++;
                }
            }

            floor_count = floorcount;
        }

        public int ChunkDataSize() {
            return Integer.BYTES * 4;
        }

        public Chunk Deserialize(World world, byte[] data) {
            if (data == null || data.length == 0)
                return null;

            ByteBuffer buffer = ByteBuffer.wrap(data);

            int x = buffer.getInt();
            int y = buffer.getInt();
            block_count = buffer.getInt();
            floor_count = buffer.getInt();

            Chunk chunk = new Chunk(new Point(x, y), world);

            byte[] blockBuffer = new byte[blockSerializer.SerializationDataSize()];

            for (int index = 0; index < block_count; index++) {
                buffer.get(blockBuffer);

                blockSerializer.Deserialize(chunk, blockBuffer);
            }

            for (int index = 0; index < floor_count; index++) {
                buffer.get(blockBuffer);

                blockSerializer.Deserialize(chunk, blockBuffer);
            }

            return chunk;
        }
    }

    public static final class ImmutableBlocks {
        private final Block[][] blocks;
        public final int length;

        private ImmutableBlocks(Block[][] blocks) {
            this.blocks = blocks;
            this.length = blocks.length;
        }

        public Block get(int x, int y) {
            return blocks[x][y];
        }
    }
}
