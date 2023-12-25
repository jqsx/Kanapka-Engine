package KanapkaEngine.Components;

import java.util.Objects;

public class NodeComponent {
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

    public void Start() {

    }
    public void Awake() {

    }
    public void onParent() {

    }
    public void onOrphan() {

    }
    public void Update() {

    }

    public final void DestroyComponent() {

    }

    public void onDestroy() {

    }

    public String toJSON() {
        return null;
    }

    private class NodeComponentData {
        public Class ClassName;
        public String JSON;
        public NodeComponentData(NodeComponent component) {
            this.ClassName = component.getClass();
            this.JSON = component.toJSON();
        }
    }
}
