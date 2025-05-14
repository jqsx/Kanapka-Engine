package KanapkaEngine.Game;

import java.util.Objects;

/**
 * The parent class to all implementations at runtime. Need to be added to a node in order to receive function calls.
 */
public abstract class Component {
    private Node parent;

    public final Node getParent() {
        return parent;
    }

    protected final void setParent(Node parent) {
        Objects.requireNonNull(parent, "Parent cannot be null.");
        if (parent.isChild(this)) {
            this.parent = parent;
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

    public final void DestroyComponent() {

    }

    public void onDestroy() {

    }
}
