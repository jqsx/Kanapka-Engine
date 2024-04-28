package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;

import java.util.Objects;

public class ChunkNode extends Node {
    private final Chunk parent;
    public Chunk getChunkParent() {
        return parent;
    }
    public ChunkNode(Chunk parent) {
        super(null);
        Objects.requireNonNull(parent);
        this.parent = parent;
    }

    @Override
    public final void append() {
        parent.appendNode(this);
    }
}
