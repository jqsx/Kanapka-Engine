package KanapkaEngine.Components;

import KanapkaEngine.Game.Chunk;
import org.joml.Vector2d;

/**
 * Interface for dynamically rendering blocks from the BlockData class when rendering chunks
 */
public interface DynamicDraw {
    void Render(Chunk chunk, Block block, vec2d chunkPosition);
}
