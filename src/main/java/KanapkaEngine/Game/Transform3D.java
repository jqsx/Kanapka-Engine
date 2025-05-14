package KanapkaEngine.Game;

import org.joml.Vector3d;
import org.joml.Vector3d;

public class Transform3D {
    private final Transform transform;

    protected Transform3D(Transform transform) {
        this.transform = transform;
    }

    public Vector3d getPosition() {
        Node node_parent = getParent();
        if (node_parent != null) {
            return transform.position.add(node_parent.transform3D().getPosition());
        }

        return transform.position.clone();
    }

    public Vector3d getLocalPosition() {
        return transform.position.clone();
    }

    public void setPosition(Vector3d position) {
        Node node_parent = getParent();
        if (node_parent != null) {
            transform.position.set(node_parent.transform3D().getPosition().sub(position));
        }
        else
            transform.position.set(position);
    }

    public void setLocalPosition(double x, double y, double z) {
        transform.position.set(x,y,z);
    }

    public void setLocalPosition(Vector3d position) {
        transform.position.set(position);
    }

    public void setPosition(double x, double y, double z) {
        Node node_parent = transform.parent.getParent();
        if (node_parent != null) {
            transform.position.set(parentPos().sub(x,y,z));
        }
        else
            transform.position.set(x,y,z);
    }

    public Vector3d getSize() {
        return new Vector3d(transform.size.x, transform.size.y, transform.size.z);
    }

    public void setSize(Vector3d size) {
        transform.size.set(size);
    }
    public void setSize(double x, double y, double z) {
        transform.size.set(x,y,z);
    }

    public double getRotation() {
        return transform.rotation.z;
    }

    public void setRotation(double rotation) {
        this.transform.rotation.z = (float)(rotation % (Math.PI * 2));
    }

    private Vector3d parentPos() {
        Node node = getParent();
        if (node == null)
            return new Vector3d();
        return node.transform3D().getPosition();
    }

    private Node getParent() {
        Node node = transform.parent.getParent();

        if (node != null) {
            return node;
        }
        return null;
    }

    public final Transformation getTransformation() {
        return transform.getTransformation();
    }
}
