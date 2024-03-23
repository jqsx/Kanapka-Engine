package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

public class ChunkNode extends Node {
    private final Chunk parent;
    public Chunk getChunkParent() {
        return parent;
    }
    public ChunkNode(Chunk parent) {
        super(null);
        this.parent = parent;
    }
}
