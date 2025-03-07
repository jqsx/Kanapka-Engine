package KanapkaEngine.Components;

import KanapkaEngine.Editor.Attributes.ReadOnly;
import KanapkaEngine.Editor.Attributes.Serialized;
import KanapkaEngine.Game.Shader;
import KanapkaEngine.Game.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Serialized
public class TextureMaterial extends Material {

    @ReadOnly
    @Uniform(uniformName = "uMainTex")
    public Texture MainTex;

    @ReadOnly
    @Uniform(uniformName = "uAtlasRes")
    public final Vector2f spriteAtlasResolution = new Vector2f(1f,1.f);

    @ReadOnly
    @Uniform(uniformName = "uAtlasIndex")
    public int atlasIndex = 0;

    public TextureMaterial() {
        super();
        setShader(Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));
    }

    @Override
    public void onSet() {

    }
}
