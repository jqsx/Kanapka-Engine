package KanapkaEngine.Components;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3d;

public class vec3d extends Vector3d implements Cloneable {
    public vec3d() {
        super();
    }
    public vec3d(Vector3dc v) {
        super(v);
    }
    public vec3d(double x, double y, double z) {
        super(x,y,z);
    }
    @Override
    public Vector3d add(Vector3dc v) {
        return add(v.x(), v.y(), v.z());
    }

    @Override
    public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Vector3d sub(Vector3dc v) {
        return sub(v.x(), v.y(), v.z());
    }

    @Override
    public Vector3d sub(double x, double y, double z) {
        return new Vector3d(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public Vector3d mul(Vector3dc v) {
        Vector3d value = new Vector3d(this.x, this.y, this.z);

        return value.mul(v.x(), v.y(), v.z());
    }

    @Override
    public Vector3d mul(double x, double y, double z) {
        Vector3d value = new Vector3d(this.x, this.y, this.z);

        return value.mul(x, y, z);
    }

    @Override
    public Vector3d mul(double scalar) {
        Vector3d value = new Vector3d(this.x, this.y, this.z);

        return value.mul(scalar);
    }

    @Override
    public Vector3d clone() {
        return new Vector3d(x, y, z);
    }
}
