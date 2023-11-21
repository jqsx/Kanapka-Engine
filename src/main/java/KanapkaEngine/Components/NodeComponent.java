package KanapkaEngine.Components;

public abstract class NodeComponent {
    private Node parent;

    public Node getParent() {
        return parent;
    }

    public final void setParent(Node parent) {
        if (parent.isChild(this)) {
            this.parent = parent;
        }
    }
}
