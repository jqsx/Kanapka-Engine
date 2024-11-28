package KanapkaEngine.Components;

import KanapkaEngine.Game.Shader;
import KanapkaEngine.Game.Texture;
import org.joml.Vector4f;

public class TextureMaterial extends Material {

    @Uniform(uniformName = "uMainTex")
    public Texture MainTex;
    @Uniform(uniformName = "uColor")
    public Vector4f color = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);

    public TextureMaterial() {
        setShader(Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));
    }
}
