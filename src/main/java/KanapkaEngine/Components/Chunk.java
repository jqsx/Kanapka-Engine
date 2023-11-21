package KanapkaEngine.Components;

import java.awt.*;

public class Chunk {
    private final Point point;
    private final Block[][] blocks = new Block[10][10];

    public final void appendBlock(Block block) {
        if (block.parent == this) {
            blocks[block.point.x][block.point.y] = block;
        }
    }

    private boolean isInRange(Point p) {
        return isInRange(p.x) && isInRange(p.y);
    }

    private boolean isInRange(int a) {
        return a <= 9 && a >= 0;
    }

    private Chunk(Point point) {
        this.point = point;
    }

    public final Point getPoint() {
        return new Point(point.x, point.y);
    }

    public static void build() {

    }
}
