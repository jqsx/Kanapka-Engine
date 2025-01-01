package KanapkaEngine.Components;

import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * A utility class that instantiates new classes of Vector2d upon operations instead of applying all the changes to the current instance
 * <br><br>
 * Only works for add, sub, and mul calls using Vector2d, double xy, or double scalar as input
 */
public class vec2d extends Vector2d implements Cloneable {
    public vec2d() {
        super();
    }
    public vec2d(Vector2d v) {
        super(v);
    }
    public vec2d(double x, double y) {
        super(x,y);
    }
    @Override
    public Vector2d add(Vector2dc v) {
        return add(v.x(), v.y());
    }

    @Override
    public Vector2d add(double x, double y) {
        return new Vector2d(this.x + x, this.y + y);
    }

    @Override
    public Vector2d sub(Vector2dc v) {
        return sub(v.x(), v.y());
    }

    @Override
    public Vector2d sub(double x, double y) {
        return new Vector2d(this.x - x, this.y - y);
    }

    @Override
    public Vector2d mul(Vector2dc v) {
        Vector2d value = new Vector2d(this.x, this.y);

        return value.mul(v.x(), v.y());
    }

    @Override
    public Vector2d mul(double x, double y) {
        Vector2d value = new Vector2d(this.x, this.y);

        return value.mul(x, y);
    }

    @Override
    public Vector2d mul(double scalar) {
        Vector2d value = new Vector2d(this.x, this.y);

        return value.mul(scalar);
    }

    @Override
    public Vector2d clone() {
        return new Vector2d(x, y);
    }
}
