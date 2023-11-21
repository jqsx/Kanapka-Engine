package KanapkaEngine.Components;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public final Transform transform;
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
        nodeComponent.setParent(this);
    }

    public final void addChild(Node child) {
        children.add(child);
    }

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
        if (parent != null)
            parent.addChild(this);
        else
            addNodeToMainScene(this);
        transform = Transform.build(this);
    }

    public static Node build(Node parent) {
        Node node = new Node(parent);
        return node;
    }

    private static void addNodeToMainScene(Node node) {

    }
}
