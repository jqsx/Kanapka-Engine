package KanapkaEngine.Components;

import KanapkaEngine.Game.*;

@WIP
public class InvertColorsPostProcess extends PostProcessEffect {

    private RenderTexture renderTexture;
    private Shader shader;

    public InvertColorsPostProcess() {
        renderTexture = new RenderTexture(1920, 1080);
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
