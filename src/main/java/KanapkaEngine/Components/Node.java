package KanapkaEngine.Components;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Node> children = new ArrayList<>();

    public final int childCount() {
        return children.size();
    }

    public final Node getChild(int i) {
        return children.get(i);
    }
}
