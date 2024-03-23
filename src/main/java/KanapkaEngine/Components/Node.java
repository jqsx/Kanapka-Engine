package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private static int NodeCount = 0;

    private boolean alive = true;
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
        if (!alive) return;
        components.add(component);
        if (component instanceof Renderer)
            renderer = (Renderer) component;
        else if (component instanceof Collider)
            collider = (Collider) component;
        else if (component instanceof Rigidbody)
            rigidbody = (Rigidbody) component;
        component.setParent(this);
    }

    public void Update() {

    }

    public final void addChild(Node child) {
        if (!alive) return;
        children.add(child);
    }

    public final void removeChild(Node child) {
        if (!alive) return;
        children.remove(child);
    }

    public final boolean isChild(Component component) {
        if (!alive) return false;
        return components.contains(component);
    }

    public final void removeComponent(Component component) {
        if (!alive) return;
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
        if (!alive) return;
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
        if (!alive) return null;
        for (Component component : components) {
            if (v.isAssignableFrom(component.getClass()))
                return (V) component;
        }
        return null;
    }

    public Node(Node parent) {
        this.parent = parent;
        if (parent != null) {
            setParent(parent);
            transform.setPosition(parent.transform.getPosition());
        }
        NodeCount++;
    }

    public Node() {
        this(null);
    }

    public final Node getParent() {
        if (!alive) return null;
        return parent;
    }

    public void setParent(Node parent) {
        if (!alive) return;
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

    public void append() {
        if (parent == null) {
            SceneManager.addNode(this);
        }
    }

    public final void Destroy() {
        if (!alive) return;
        alive = false;
        onDestroy();
        children.forEach(Node::Destroy);
        children.clear();
        NodeCount--;
        components.forEach(Component::onOrphan);
        components.clear();
    }

    public void onDestroy() {

    }

    public static int getNodeCount() {
        return NodeCount;
    }

    public final Renderer getRenderer() {
        if (!alive) return null;
        return renderer;
    }
    public final Collider getCollider() {
        if (!alive) return null;
        return collider;
    }

    public final Rigidbody getRigidbody() {
        if (!alive) return null;
        return rigidbody;
    }

    public final void UpdateCall() {
        if (!alive) return;
        Update();
        for (Component component : components) {
            component.Update();
        }
        synchronized (children) {
            children.removeIf(child -> !child.isAlive());
        }
        for (Node child : children) {
            child.UpdateCall();
        }
    }

    public final boolean isAlive() {
        return alive;
    }
}
