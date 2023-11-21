package KanapkaEngine.Components;

import java.awt.*;

public abstract class Block {
    public final Chunk parent;
    public final Point point;
    public abstract void Render(Graphics2D chunkVisual, Point chunkBlockPoint);

    public Block(Chunk parent, Point point) {
        this.point = point;
        this.parent = parent;
        parent.appendBlock(this);
    }
}
