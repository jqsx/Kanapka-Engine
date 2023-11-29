package KanapkaEngine.Components;

import java.util.Objects;

public abstract class NodeComponent {
    private Node parent;

    public final Node getParent() {
        return parent;
    }

    public final void setParent(Node parent) {
        Objects.requireNonNull(parent, "Parent cannot be null.");
        if (parent.isChild(this)) {
            onOrphan();
            this.parent = parent;
            System.out.println(parent.name);
            Awake();
            onParent();
        }
    }

    abstract void Start();
    void Awake() {

    }
    void onParent() {

    }
    void onOrphan() {

    }
    abstract void Update();

    public final void DestroyComponent() {

    }

    abstract void onDestroy();

    abstract String toJSON();

    private class NodeComponentData {
        public Class ClassName;
        public String JSON;
        public NodeComponentData(NodeComponent component) {
            this.ClassName = component.getClass();
            this.JSON = component.toJSON();
        }
    }
}
