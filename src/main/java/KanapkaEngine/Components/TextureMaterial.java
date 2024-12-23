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
                colors[cindex+i] = lerp(main_colors[index], white, i / (float)dillusion);
            }
        }
    }

    private static float lerp(float a, float b, float t) {
        t = Math.min(Math.max(t, 0.0f), 1.0f);

        return a + (b - a) * t;
    }

    private static Vector4f lerp(Vector4f a, Vector4f b, float t) {
        return new Vector4f(lerp(a.x, b.x, t), lerp(a.y, b.y, t),lerp(a.z, b.z, t),lerp(a.w, b.w, t));
    }

    public TextureMaterial() {
        setShader(Shader.findOrCreate("builtIn:texture", "Shader/standard/texture"));

        getShader().createUniform("uColors");
    }

    @Override
    public void onSet() {
        getShader().setUniformArray("uColors", colors);
    }
}
