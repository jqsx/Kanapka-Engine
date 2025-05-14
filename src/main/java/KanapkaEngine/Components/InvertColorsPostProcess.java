package KanapkaEngine.Components;

import KanapkaEngine.Game.*;

import static org.lwjgl.opengl.GL11.*;

@WIP
public class InvertColorsPostProcess extends PostProcessEffect {

    private RenderTexture renderTexture;
    private Shader shader;

    public InvertColorsPostProcess() {
        renderTexture = new RenderTexture(1920, 1080);
        shader = Shader.findOrCreate("builtin:post:inverse", "Shader/standard/PostProcess/Invert");
    }

    @Override
    public RenderTexture Render(Texture texture) {
        renderTexture.bind();

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        Graphics.DrawFullScreen(texture, shader);

        renderTexture.unbind();

        return renderTexture;
    }
}
