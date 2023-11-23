package KanapkaEngine.Components;

public abstract class NodeComponent {
    private Node parent;

    public final Node getParent() {
        return parent;
    }

    public final void setParent(Node parent) {
        if (parent == null) return;
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
