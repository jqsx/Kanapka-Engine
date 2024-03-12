package KanapkaEngine.Components;

public class ChunkNode extends Node {
    private final Chunk parent;
    public Chunk getChunkParent() {
        return parent;
    }
    public ChunkNode(Chunk parent) {
        this.parent = parent;
    }
}
