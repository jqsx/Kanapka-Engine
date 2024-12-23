package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.World;
import KanapkaEngine.Game.*;
import org.joml.Vector2d;

public class ChunkLayer implements RenderLayer {
    @Override
    public void Render() {
        Chunk chunk = World.getCurrent().get(0, 0);

        if (chunk != null) {
            Texture texture = chunk.getTexture();
            if (texture != null)
                Graphics.DrawSprite(chunk.getTexture(), new Transformation(chunk.getPosition(), new Vector2d(1.0, 1.0), 0.0f), Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.BACKGROUND;
    }
}
