package KanapkaEngine.Components;

import KanapkaEngine.Game.Shader;
import KanapkaEngine.Game.Texture;
import org.joml.Vector4f;

public class TextureMaterial extends Material {

    @Uniform(uniformName = "uMainTex")
    public Texture MainTex;

    private static Vector4f[] colors;

    private final static Vector4f white = new Vector4f(1.f, 1.f, 1.f, 1.f);

    static {
        int dillusion = 8;

        Vector4f[] main_colors = {
                new Vector4f(1.f, 0.f, 0.f, 1.f),
                new Vector4f(0.f, 1.f, 0.f, 1.f),
                new Vector4f(0.f, 0.f, 1.f, 1.f),
                new Vector4f(1.f, 1.f, 0.f, 1.f),
                new Vector4f(0.f, 1.f, 1.f, 1.f),
                new Vector4f(1.f, 0.f, 1.f, 1.f),
                new Vector4f(1.f, 1.f, 1.f, 1.f),
                new Vector4f(0.f, 0.f, 0.f, 1.f),
        };

        colors = new Vector4f[main_colors.length * dillusion];

        for (int index = 0; index < main_colors.length; index++) {

            int cindex = index * dillusion;

            for (int i = 0; i < dillusion; i++) {
                colors[cindex+i] = main_colors[index].lerp(white, i / (float)dillusion);
            }
        }
    }

    public TextureMaterial() {
        setShader(Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));

        //getShader().createUniform("uColors");
    }

    @Override
    public void onSet() {
        getShader().setUniformArray("uColors", colors);
    }
}
