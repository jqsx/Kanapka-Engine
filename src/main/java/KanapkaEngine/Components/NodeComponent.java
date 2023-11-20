package KanapkaEngine.Components;

public class NodeComponent {
    private Node parent;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        if (parent.isChild(this)) {
            this.parent = parent;
        }
    }
}
