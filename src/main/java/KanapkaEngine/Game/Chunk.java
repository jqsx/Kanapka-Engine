package KanapkaEngine.Game;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;

public class Chunk {
    /**
     * Hardcoded block scale
     */

    private static final Logger logger = new Logger("CHUNK");

    public static final int BLOCK_SCALE = 16;
    private Rectangle2D bounds;
    protected Texture renderTexture;
    private BufferedImage render;
    private Renderer.Stage render_stage = Renderer.Stage.NOTSTARTED;
    private final World parent;
    private final Point point;
    private final Block[][] blocks;
    private final ImmutableBlocks immutableBlocks;
    private boolean isReadyForRender = false;
    private boolean isActive = false;
    private boolean needReRender = false;
    private long lastActive = System.currentTimeMillis();
    private final LinkedList<ChunkNode> chunkNodeList = new LinkedList<>();

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
        if (block.parent == this && isInRange(block.point)) {
            if (old == null) needReRender = true;
            else if (old.id != block.id) needReRender = true;
            blocks[block.point.x][block.point.y] = block;
        }
    }

    /**
     * Appends a ChunkNode to the chunk.
     * @param node
     */
    public final void appendNode(ChunkNode node) {
        Objects.requireNonNull(node);
        if (node.getChunkParent() == this)
            if (!chunkNodeList.contains(node))
                chunkNodeList.add(node);
    }

    public final void removeNode(ChunkNode node) {
        chunkNodeList.remove(node);
    }

    public final void removeNode(int i) {
        chunkNodeList.remove(i);
    }

    /**
     * Setting the block at point to air.
     * @param p
     */
    public final void setAir(Point p) {
        if (isInRange(p)) {
            Block old = blocks[p.x][p.y];
            if (old != null) {
                blocks[p.x][p.y] = null;
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
            immutableBlocks = new ImmutableBlocks(blocks);
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
        return immutableBlocks;
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
    }

    private void deactivate() {
        if (isActive && render != null) {

        }
            if (lastActive + 50L < System.currentTimeMillis()) {
                render_stage = Renderer.Stage.NOTSTARTED;
                isActive = false;
                render.flush();
                render = null;
                bounds = null;
            }
    }

    public final boolean IsActive() {
        return isActive;
    }

    public final void CheckDeactivation() {
        deactivate();
    }

    private void beginRender() {
        if (!isReadyForRender && !needReRender) return;
        if (needReRender)
            needReRender = false;
        render_stage = Renderer.Stage.RENDERING;
        new Thread(() -> {
            int s = SceneManager.getCurrentlyLoaded().getChunkSize() * BLOCK_SCALE;
            BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            for (Block[] column : blocks)
                for (Block block : column) {
                    if (block == null) continue;
                    BufferedImage block_render = block.getRender();
                    AffineTransform at = getAffineTransform(block, block_render);
                    g.drawImage(block_render, at, null);
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

    private void Update() {
        chunkNodeList.forEach(ChunkNode::UpdateCall);
    }

    /**
     * Internal Update Call
     */
    protected static void UpdateChunks() {
        try {
//            Chunks.getActiveChunks().foreach(Chunk::Update);
        } catch (ConcurrentModificationException ignore) {

        }
    }

    public final LinkedList<ChunkNode> getChunkNodes() {
        return chunkNodeList;
    }

    /**
     * Im not spoon feeding you here I had to write this reader and now its your turn to live the nightmare
     * <br><br>
     * Now how does this save the block data in the first place: the blocks have their point, id, and special id buffered. The point object is buffered as 2 bytes in order to save on bytes therefore limiting chunk size to 127x127 which they probably shouldn't be as it defeats the point of the system in the first place. If the block value is null then its not buffered and its skipped.
     */
    public static class ChunkSerializer {
        private static final ByteBuffer _buffer = ByteBuffer.allocate(12);

        private int block_count = 0;

        public byte[] Serialize(Chunk chunk) {
            ImmutableBlocks temp_blocks = chunk.getBlocks();
            CalculateBlockCount(temp_blocks);
            ByteBuffer buffer = ByteBuffer.allocate(ChunkDataSize() + block_count * blockSerializer.SerializationDataSize());

            buffer.put(ChunkData(chunk));
            for (int x = 0; x < temp_blocks.length; x++) {
                for (int y = 0; y < temp_blocks.length; y++) {
                    Block value = temp_blocks.get(x,y);
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

        public int ChunkDataSize() {
            return 12;
        }

        public Chunk Deserialize(World world, byte[] data) {
            _buffer.put(data, 0, ChunkDataSize());

            int x = _buffer.getInt();
            int y = _buffer.getInt();
            block_count = _buffer.getInt();

            Chunk chunk = new Chunk(new Point(x, y), world);

            ByteBuffer buffer = ByteBuffer.wrap(data);
            byte[] blockBuffer = new byte[blockSerializer.SerializationDataSize()];

            buffer.position(ChunkDataSize() - 1);

            for (int index = 0; index < block_count; index++) {
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
