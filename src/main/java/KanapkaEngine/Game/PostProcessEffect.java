package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

@WIP
public class PostProcessEffect {
    public RenderTexture renderTexture;
    public Shader shader;

    public RenderTexture Render(Texture texture) {
        if (shader == null || renderTexture == null)
            return null;

        renderTexture.bind();

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        Graphics.DrawFullScreen(texture, shader);

        renderTexture.unbind();

        return renderTexture;
    }
}
