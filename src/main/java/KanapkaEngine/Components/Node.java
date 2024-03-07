package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private static int NodeCount = 0;
    public String name = "Node_Instance";
    private Node parent;
    public final Transform transform = new Transform(this);
    private Renderer renderer;
    private Collider collider;
    private Rigidbody rigidbody;
    private final List<Node> children = new ArrayList<>();

    public final int childCount() {
        return children.size();
    }

    public final Node getChild(int i) {
        return children.get(i);
    }

    private final List<Component> components = new ArrayList<>();

    public final void addComponent(Component component) {
        components.add(component);
        if (component instanceof Renderer)
            renderer = (Renderer) component;
        else if (component instanceof Collider)
            collider = (Collider) component;
        else if (component instanceof Rigidbody)
            rigidbody = (Rigidbody) component;
        component.setParent(this);
    }

    public final void addChild(Node child) {
        children.add(child);
    }

    public final void removeChild(Node child) { children.remove(child); }

    public final boolean isChild(Component component) {
        return components.contains(component);
    }

    public final void removeComponent(Component component) {
        boolean s = components.remove(component);
        if (s) {
            if (renderer == component)
                renderer = null;
            else if (collider == component)
                collider = null;
            else if (rigidbody == component)
                rigidbody = null;
            component.onOrphan();
        }
    }

    public final void removeComponent(int i) {
        if (i >= components.size()) return;
        Component component = components.remove(Math.abs(i));
        if (component != null) {
            if (renderer == component)
                renderer = null;
            else if (collider == component)
                collider = null;
            else if (rigidbody == component)
                rigidbody = null;
            component.onOrphan();
        }
    }

    public final <V extends Component> V getComponent(Class<V> v) {
        for (Component component : components) {
            if (v.isAssignableFrom(component.getClass()))
                return (V) component;
        }
        return null;
    }

    public Node(Node parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
            transform.setPosition(parent.transform.getPosition());
        }
        else
            setParent(null);
        NodeCount++;
    }

    public Node() {
        this(null);
    }

    public final Node getParent() {
        return parent;
    }

    public final void setParent(Node parent) {
        if (this.parent != null)
            this.parent.removeChild(this);
        else
            SceneManager.removeNode(this);
        this.parent = parent;
        if (parent != null)
            parent.addChild(this);
        else
            SceneManager.addNode(this);
    }

    public final void Destroy() {
        if (parent != null)
            parent.removeChild(this);
        else
            SceneManager.removeNode(this);
        NodeCount--;
        for (Component component : components) {
            component.onOrphan();
        }
        components.clear();
    }

    public static int getNodeCount() {
        return NodeCount;
    }

    public final Renderer getRenderer() {
        return renderer;
    }
    public final Collider getCollider() { return collider; }

    public final Rigidbody getRigidbody() {
        return rigidbody;
    }

    public final void UpdateCall() {
        for (Component component : components) {
            component.Update();
        }
        for (Node child : children) {
            child.UpdateCall();
        }
    }
}
