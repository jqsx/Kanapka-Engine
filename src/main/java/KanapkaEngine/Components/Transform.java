package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Transform {
    public final Node parent;
    public Vector2D position = new Vector2D(0, 0);
    public double rotation = 0;
    public Vector2D size = new Vector2D(1, 1);

    private Transform(Node parent) {
        this.parent = parent;
    }

    public static Transform build(Node parent) {
        if (parent.transform == null)
            return new Transform(parent);
        return null;
    }
}