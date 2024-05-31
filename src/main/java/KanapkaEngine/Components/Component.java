package KanapkaEngine.Components;

import java.util.Objects;

/**
 * The parent class to all implementations at runtime. Need to be added to a node in order to receive function calls.
 */
public class Component {
    private Node parent;

    public final Node getParent() {
        return parent;
    }

    public final void setParent(Node parent) {
        Objects.requireNonNull(parent, "Parent cannot be null.");
        if (parent.isChild(this)) {
            onOrphan();
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
    public void Update() {

    }

    public final void DestroyComponent() {

    }

    public void onDestroy() {

    }
}
