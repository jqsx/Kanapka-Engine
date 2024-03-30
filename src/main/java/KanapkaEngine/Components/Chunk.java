package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.RenderLayers.ChunkNodes;
import KanapkaEngine.RenderLayers.Chunks;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Chunk {
    public static final int BLOCK_SCALE = 16;
    private Rectangle2D bounds;
    private BufferedImage render;
    private int render_stage = 0;
    private final World parent;
    private final Point point;
    private final Block[][] blocks;

    private boolean isReadyForRender = false;

    private boolean isActive = false;
    private boolean needReRender = false;

    private long lastActive = System.currentTimeMillis();

    private final LinkedList<ChunkNode> chunkNodeList = new LinkedList<>();

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

    public final void appendNode(ChunkNode node) {
        if (!chunkNodeList.contains(node))
            chunkNodeList.add(node);
    }

    public final void removeNode(ChunkNode node) {
        chunkNodeList.remove(node);
    }

    public final void removeNode(int i) {
        chunkNodeList.remove(i);
    }

    public final void setAir(Point p) {
        if (isInRange(p)) {
            Block old = blocks[p.x][p.y];
            if (old != null) {
                blocks[p.x][p.y] = null;
                needReRender = true;
            }
        }
    }

    public final Block createBlock(int id, Point p) {
        if (id < 0) {
            setAir(p);
            return null;
        }
        return new Block(this, p, id);
    }

    public Rectangle2D getBounds() {
        Vector2D camera_position = Camera.main.getPosition();
        Vector2D position = getPosition();
        double g_size = SceneManager.getGlobalSize();
        Vector2D pos = new Vector2D((camera_position.getX() + position.getX()), -(camera_position.getY() + position.getY()));
        if (bounds == null)
            bounds = new Rectangle2D.Double(pos.getX(), pos.getY(), render.getWidth(), render.getHeight());
        else
            bounds.setFrame(pos.getX(), pos.getY(), render.getWidth(), render.getHeight());
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
        if (SceneManager.hasScene())
            blocks = new Block[SceneManager.getCurrentlyLoaded().getChunkSize()][SceneManager.getCurrentlyLoaded().getChunkSize()];
        else
            throw new RuntimeException("No scene loaded.");
        parent.set(this);
    }

    public Vector2D getPosition() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2D(point.x * s, point.y * s);
    }

    public Vector2D getBlockPosition(Point p) {
        return getPosition().add(new Vector2D(p.x, -p.y).scalarMultiply(BLOCK_SCALE));
    }

    public static Vector2D getSize() {
        int s = BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize();
        return new Vector2D(s, s);
    }

    public final Point getPoint() {
        return new Point(point.x, point.y);
    }

    public BufferedImage getRender() {
        if (!isActive) return null;
        if (render_stage == Renderer.NOT_STARTED || needReRender) beginRender();
        if (render_stage == Renderer.FINISHED) return render;
        else return null;
    }

    public final void activate() {
        isActive = true;
        lastActive = System.currentTimeMillis();
    }

    private void deactivate() {
        if (isActive && render != null)
            if (lastActive + 1000L * Chunks.DEACTIVATIONDELAY < System.currentTimeMillis()) {
                render_stage = Renderer.NOT_STARTED;
                isActive = false;
                render.flush();
                render = null;
                bounds = null;
                System.out.println("Deactivated Chunk x: " + point.x + " y: " + point.y);
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
            render_stage = Renderer.FINISHED;
        }).start();
    }

    public final void ready() {
        isReadyForRender = true;
    }

    private AffineTransform getAffineTransform(Block block, BufferedImage block_render) {
        if (block_render == null)
            return new AffineTransform();
        AffineTransform at = new AffineTransform();
        Vector2D p = new Vector2D(block.point.x * BLOCK_SCALE, block.point.y * BLOCK_SCALE);
        if (block.getBlockData().scale_render) {
            at.scale(BLOCK_SCALE / (double) block_render.getWidth(), BLOCK_SCALE / (double) block_render.getHeight());
            at.translate(p.getX(), p.getY());
        }
        else {
            Vector2D m = new Vector2D(BLOCK_SCALE / 2.0 - block_render.getWidth() / 2.0, BLOCK_SCALE - block_render.getHeight() / 2.0);
            at.translate(block.point.x * BLOCK_SCALE + m.getX(), block.point.y * BLOCK_SCALE + m.getY());
        }
        return at;
    }

    private void scaleAt(AffineTransform at, BufferedImage image) {
        at.translate(image.getWidth() / 2.0, image.getHeight() / 2.0);
        at.scale(1, -1);
        at.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
    }

    private void finishedRender() {
        render_stage = Renderer.FINISHED;
    }


    public static Chunk build(Point point, World parent) {
        return new Chunk(Objects.requireNonNull(point, "Missing chunk point."), Objects.requireNonNull(parent, "Missing parent."));
    }

    public World getParent() {
        return parent;
    }

    public byte[] getSave() {
        if (!isReadyForRender) {
            ByteBuffer buffer = ByteBuffer.allocate(12);
            buffer.putInt(point.x);
            buffer.putInt(point.y);
            buffer.putInt(0);
            return buffer.array();
        }
        Block[][] temp_blocks = Arrays.copyOf(blocks, blocks.length);
        System.out.println("Getting save for chunk x: " + point.x + " y: " + point.y);
        int block_count = 0;
        for (Block[] block : temp_blocks) {
            for (Block value : block) {
                if (value != null)
                    block_count++;
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(12 + block_count * 10);
        System.out.println("Buffer size: " + buffer.capacity());
        System.out.println("Block count: " + block_count);

        buffer.putInt(point.x);
        buffer.putInt(point.y);
        buffer.putInt(block_count);
        for (Block[] block : temp_blocks) {
            for (Block value : block) {
                if (value == null) continue;
                byte x = (byte) Mathf.Clamp(value.point.x, 0, 127);
                System.out.println(x);
                byte y = (byte) Mathf.Clamp(value.point.y, 0, 127);
                System.out.println(y);
                buffer.put(x);
                buffer.put(y);
                buffer.putInt(value.id);
                buffer.putInt(value.special_id);
                System.out.println("Limit: " + buffer.remaining());
            }
        }

        return buffer.array();
    }

    private void Update() {
        chunkNodeList.forEach(ChunkNode::UpdateCall);
    }

    public static void UpdateChunks() {
        try {
            Chunks.getActiveChunks().forEach(Chunk::Update);
        } catch (ConcurrentModificationException ignore) {

        }
    }

    public final LinkedList<ChunkNode> getChunkNodes() {
        return chunkNodeList;
    }
}
