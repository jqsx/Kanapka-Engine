package KanapkaEngine.Game;

import KanapkaEngine.Components.vec2d;
import KanapkaEngine.Components.vec3d;
import KanapkaEngine.Game.Node;
import KanapkaEngine.Game.Transformation;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.awt.geom.AffineTransform;

public class Transform {
    private final Transformation transformation;
    public final Node parent;
    protected final vec3d position = new vec3d(0, 0,0);
    protected final Vector3f rotation = new Vector3f();
    protected final Vector3d size = new Vector3d(1, 1, 1);

    protected final Transform2D transform2D;
    protected final Transform3D transform3D;

    protected Transform(Node parent) {
        this.parent = parent;
        transformation = new Transformation(new Vector3d(), new Vector3d(1, 1, 1), new Vector3f());

        transform2D = new Transform2D(this);
        transform3D = new Transform3D(this);
    }

    public Transformation getTransformation() {
        transformation.Update(position, size, rotation);

        return transformation;
    }
}