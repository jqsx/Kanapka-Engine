package KanapkaEngine.Components;

import java.util.HashMap;
import java.util.Objects;

public class World {
    private HashMap<Integer, HashMap<Integer, Chunk>> chunk_map = new HashMap<>();

    public final Chunk get(int x, int y) {
        if (chunk_map.containsKey(x))
            return chunk_map.get(x).get(y);
        else return null;
    }

    public final void set(Chunk chunk) {
        Objects.requireNonNull(chunk, "Chunk cannot be null.");
        if (chunk.getParent() == this) {
            if (!chunk_map.containsKey(chunk.getPoint().x))
                chunk_map.put(chunk.getPoint().x, new HashMap<>());
            chunk_map.get(chunk.getPoint().x).put(chunk.getPoint().y, chunk);
        }
    }
}
