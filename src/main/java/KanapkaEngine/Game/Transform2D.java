package KanapkaEngine.Game;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class Transform2D {
    private final Transform transform;

    protected Transform2D(Transform transform) {
        this.transform = transform;
    }

    public Vector2d getPosition() {
        Node node_parent = transform.parent.getParent();
        if (node_parent != null) {
            return toVec2d(transform.position).add(getParentPos2D());
        }

        return toVec2d(transform.position);
    }

    public Vector2d getLocalPosition() {
        return toVec2d(transform.position);
    }

    public void setPosition(Vector2d position) {
        Node node_parent = transform.parent.getParent();
        if (node_parent != null) {
            Vector2d v = getParentPos2D().sub(position);
            set(transform.position, v.x, v.y);
        }
        else
            set(transform.position, position.x, position.y);
    }

    public void setLocalPosition(double x, double y) {
        set(transform.position, x,y);
    }

    public void setLocalPosition(Vector2d position) {
        set(transform.position, position.x, position.y);
    }

    public void setPosition(double x, double y) {
        Node node_parent = transform.parent.getParent();
        if (node_parent != null) {
            set(transform.position, getParentPos2D().sub(x, y));
        }
        else
            set(transform.position, x, y);
    }

    public Vector2d getSize() {
        return toVec2d(transform.size);
    }

    public void setSize(Vector2d size) {
        set(transform.size, size);
    }
    public void setSize(double x, double y) {
        set(transform.size, x,y);
    }

    public double getRotation() {
        return transform.rotation.z;
    }

    public void setRotation(double rotation) {
        this.transform.rotation.z = (float)(rotation % (Math.PI * 2));
    }

    private Vector2d toVec2d(Vector3d vector3d) {
        return new Vector2d(vector3d.x, vector3d.y);
    }

    private void set(Vector3d it, Vector2d to) {
        it.x = to.x;
        it.y = to.y;
    }

    private void set(Vector3d it, double x, double y) {
        it.set(x,y, it.z);
    }

    private Vector2d getParentPos2D() {
        Node node = transform.parent.getParent();

        if (node == null)
            return new Vector2d();

        return node.transform2D().getPosition();
    }

    public final Transformation getTransformation() {
        return transform.getTransformation();
    }
}
