package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private static int NodeCount = 0;
    public String name = "Node_Instance";
    private Node parent;
    public final Transform transform = Transform.build(this);
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

    private final List<NodeComponent> components = new ArrayList<>();

    public final void addComponent(NodeComponent nodeComponent) {
        components.add(nodeComponent);
        if (nodeComponent instanceof Renderer)
            renderer = (Renderer) nodeComponent;
        else if (nodeComponent instanceof Collider)
            collider = (Collider) nodeComponent;
        else if (nodeComponent instanceof Rigidbody)
            rigidbody = (Rigidbody) nodeComponent;
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
        boolean s = components.remove(nodeComponent);
        if (s) {
            if (renderer == nodeComponent)
                renderer = null;
            else if (collider == nodeComponent)
                collider = null;
            else if (rigidbody == nodeComponent)
                rigidbody = null;
            nodeComponent.onOrphan();
        }
    }

    public final void removeComponent(int i) {
        if (i >= components.size()) return;
        NodeComponent nodeComponent = components.remove(Math.abs(i));
        if (nodeComponent != null) {
            if (renderer == nodeComponent)
                renderer = null;
            else if (collider == nodeComponent)
                collider = null;
            else if (rigidbody == nodeComponent)
                rigidbody = null;
            nodeComponent.onOrphan();
        }
    }

    public final <V extends NodeComponent> V getComponent(Class<V> v) {
        for (NodeComponent component : components) {
            if (v.isAssignableFrom(component.getClass()))
                return (V) component;
        }
        return null;
    }

    private Node(Node parent) throws Exception {
        this.parent = parent;
        if (parent != null)
            parent.addChild(this);
        else
            SceneManager.addNode(this);
    }

    public static Node build(Node parent) throws Exception {
        Node child = new Node(parent);
        if (parent != null)
            child.transform.setPosition(parent.transform.getPosition());
        NodeCount++;
        return child;
    }

    public static Node build() throws Exception {
        NodeCount++;
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

    public void Destroy() {
        if (parent != null)
            parent.removeChild(this);
        else
            SceneManager.removeNode(this);
        NodeCount--;
        for (int i = 0; i < components.size(); i++) {
            components.get(i).onOrphan();
        }
    }

    public static int getNodeCount() {
        return NodeCount;
    }

    public Renderer getRenderer() {
        return renderer;
    }
    public Collider getCollider() { return collider; }

    public Rigidbody getRigidbody() {
        return rigidbody;
    }

    public final void UpdateCall() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).Update();
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).UpdateCall();
        }
    }

    class NodeData {
        private static int globalIndex = 0;

        public int[] path;

        public NodeData(Node node) {

        }
    }
}
