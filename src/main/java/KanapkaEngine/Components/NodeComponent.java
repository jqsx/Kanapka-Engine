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
            this.parent = parent;
            System.out.println(parent.name);
            Awake();
        }
    }

    abstract void Start();
    void Awake() {

    }
    abstract void Update();

    public final void DestroyComponent() {

    }

    abstract void onDestroy();
}
