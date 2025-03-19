package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Game.*;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class ChunkLayer implements RenderLayer {

    private static final vec2d m_globalChunkPosition = new vec2d();

    private static final int m_chunkScale = SceneManager.getCurrentlyLoaded().getChunkSize();

    private static final Vector2d m_chunkScalev2d = new Vector2d(m_chunkScale, m_chunkScale);

    private static final Rectangle2D.Double m_Bounds = new Rectangle2D.Double();

    private final Vector2i m_CameraChunk = new Vector2i();

    private final static Vector2f uAtlasRes = new Vector2f(1.f, 1.f);

    @Override
    public void Render() {
        if (Camera.main == null)
            return;

        Vector2d cameraPos = Camera.main.getPosition();

        m_CameraChunk.set((int)floor((cameraPos.x + m_chunkScale / 2.0) / m_chunkScale), (int)floor((cameraPos.y + m_chunkScale / 2.0) / m_chunkScale));

        int renderDistance = 3;

        Shader.Standard.getTextureShader().setUniform("uAtlasIndex", 0);
        Shader.Standard.getTextureShader().setUniform("uAtlasRes", uAtlasRes);

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int y = -renderDistance; y <= renderDistance; y++) {
                Chunk chunk = World.getCurrent().get(m_CameraChunk.x+x, m_CameraChunk.y+y);

                if (chunk != null) {
                    Texture texture = chunk.getTexture();
                    if (texture != null)
                        Graphics.DrawSprite(chunk.getTexture(), getPositionForChunk(m_CameraChunk.x+x, m_CameraChunk.y+y), m_chunkScalev2d, 0f);

                    RenderDynamicDrawBlocks(chunk);

                    chunk.activate();
                }
            }
        }
    }

    private void RenderDynamicDrawBlocks(Chunk chunk) {
        Chunk.ImmutableBlocks blocks = chunk.getBlocks();
        for (int x = 0; x < m_chunkScale; x++) {
            for (int y = 0; y < m_chunkScale; y++) {
                Block block = blocks.get(x, y);

                if (block != null && block.getBlockData() instanceof DynamicDraw dynamicDraw) {
                    dynamicDraw.Render(chunk, block, getPositionForChunk(chunk.getPoint().x, chunk.getPoint().y));
                }
            }
        }
    }

    private double floor(double in) {
        boolean lessThanZero = in < 0;

        return Math.floor(in) + (lessThanZero ? -0.5 : 0);
    }

    private vec2d getPositionForChunk(int x, int y) {
        m_globalChunkPosition.set(x * m_chunkScale, y * m_chunkScale);
        return m_globalChunkPosition;
    }

    private Rectangle2D.Double getBoundsForChunk(int x, int y) {

        Vector2d position = getPositionForChunk(x, y);

        m_Bounds.setRect(position.x - m_chunkScale / 2.0, position.y - m_chunkScale / 2.0, m_chunkScale, m_chunkScale);

        return m_Bounds;
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.BACKGROUND;
    }
}
