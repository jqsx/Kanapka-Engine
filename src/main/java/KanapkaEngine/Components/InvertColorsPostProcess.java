package KanapkaEngine.Components;

import KanapkaEngine.Game.*;

public class InvertColorsPostProcess extends PostProcessEffect {

    private RenderTexture renderTexture;
    private Shader shader;

    public InvertColorsPostProcess() {
        renderTexture = new RenderTexture();
        shader = Shader.findOrCreate("builtin:post:inverse", "Shader/standard/PostProcess/Invert");
    }

    @Override
    public Texture Render(Texture inout, Window window) {
        renderTexture.bind();

        Graphics.DrawFullScreen(inout, shader);

        renderTexture.unbind();

        return renderTexture.getTexture();
    }
}
