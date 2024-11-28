package KanapkaEngine.Components;

import KanapkaEngine.Game.Shader;

public class StandardMaterial extends Material {

    @Uniform(uniformName = "uExample")
    public float exampleValue = 2.f;

    public StandardMaterial() {
        super();

        setShader(Shader.findOrCreate("builtIn:standard", "Shader/standard/standard"));
    }
}
