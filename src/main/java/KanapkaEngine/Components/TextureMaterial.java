package KanapkaEngine.Components;

import KanapkaEngine.Game.Shader;
import KanapkaEngine.Game.Texture;
import org.joml.Vector4f;

public class TextureMaterial extends Material {

    @Uniform(uniformName = "uMainTex")
    public Texture MainTex;

    public TextureMaterial() {
        setShader(Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));
    }
}
