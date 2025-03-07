package KanapkaEngine.Game;

import KanapkaEngine.Components.ImmutableList;
import KanapkaEngine.Components.World;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    protected final List<Node> nodes = new ArrayList<>();
    private final ImmutableList<Node> immutableNodes = new ImmutableList<>(nodes);

    public final World scene_world;

    public boolean isZSorted = false;

    private int CHUNK_SIZE = 10;

    public Scene() {
        scene_world = new World();
    }

    public Scene(World world) {
        this.scene_world = world;
    }

    public int getChunkSize() {
        return CHUNK_SIZE;
    }

    /**
     * NOT RECOMMENDED TO CHANGE DURING RUN TIME!!!
     * CHUNKS WILL GET MESSED UP AND CHUNKS WILL BE IN THE WRONG
     * PLACES!!!
     *
     * RUN ONLY BEFORE WORLD CREATION!!!
     * @param CHUNK_SIZE
     */
    public void setChunkSize(int CHUNK_SIZE) {
        this.CHUNK_SIZE = CHUNK_SIZE;
    }

    protected void zSort() {
        int count = nodes.size();

        // Progressive bubble sort

        if (count > 1 && isZSorted)
            for (int i = 0; i < count - 1; i++) {
                Node one = nodes.get(i);
                Node two = nodes.get(i+1);

                if (one.transform.getPosition().y > two.transform.getPosition().y) {
                    nodes.set(i, two);
                    nodes.set(i+1, one);
                }
            }
    }

    public final ImmutableList<Node> getNodes() {
        return immutableNodes;
    }

    public final void appendNode(Node node) {
        if (node.getParent() != null)
            node.setParent(null);
        else if (!nodes.contains(node))
            nodes.add(node);
    }

    public final void removeNode(Node node) {
        nodes.remove(node);
    }
}
