package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String name = "Node_Instance";
    private Node parent;
    public final Transform transform = Transform.build(this);
    private Renderer renderer;
    private final List<Node> children = new ArrayList<>();

    public final int childCount() {
        return children.size();
    }

    public final Node getChild(int i) {
        return children.get(i);
    }

    private final List<NodeComponent> components = new ArrayList<>();

    public final void addComponent(NodeComponent nodeComponent) {
        components.add(nodeComponent);
        if (nodeComponent instanceof Renderer)
            renderer = (Renderer) nodeComponent;
        nodeComponent.setParent(this);
    }

    public final void addChild(Node child) {
        children.add(child);
    }

    public final void removeChild(Node child) { children.remove(child); }

    public final boolean isChild(NodeComponent nodeComponent) {
        return components.contains(nodeComponent);
    }

    public final void removeComponent(NodeComponent nodeComponent) {
        components.remove(nodeComponent);
    }

    public final <V extends NodeComponent> V getComponent(Class<V> v) {
        for (NodeComponent component : components) {
            if (component.getClass() == v)
                return (V) component;
        }
        return null;
    }

    private Node(Node parent) {
        this.parent = parent;
        if (parent != null)
            parent.addChild(this);
        else
            SceneManager.addNode(this);
    }

    public static Node build(Node parent) {
        return new Node(parent);
    }

    public static Node build() {
        return new Node(null);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
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

    public Renderer getRenderer() {
        return renderer;
    }
}
