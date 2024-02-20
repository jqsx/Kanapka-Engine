package KanapkaEngine.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Block {
    public final Chunk parent;
    public final Point point;
    public int id = 0;

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

    public final BufferedImage getRender() {
        return BlockManager.getBlockData(id).getRender();
    }

    public final BlockData getBlockData() {
        return BlockManager.getBlockData(id);
    }
}
