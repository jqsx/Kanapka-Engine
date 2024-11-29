package KanapkaEngine.Game;

import KanapkaEngine.Components.Material;
import KanapkaEngine.Components.TextureMaterial;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public final class Graphics {

    private final static Matrix4f model = new Matrix4f().identity();

    private static Mesh spriteMesh;

    public static void DrawMesh(Mesh mesh, Transformation transformation, Shader shader) {
        DrawMesh(mesh.attributeBuffer, transformation, shader);
    }

    public static void DrawMesh(Mesh mesh, Transformation transformation, Material shader) {
        Objects.requireNonNull(shader);

        shader.Set();

        DrawMesh(mesh.attributeBuffer, transformation, shader.getShader());
    }

    public static void DrawMesh(AttributeBuffer mesh, Transformation transformation, Shader shader) {
        Objects.requireNonNull(shader);
        Objects.requireNonNull(mesh);
        Objects.requireNonNull(transformation);
        if (Camera.main == null)
            return;

        shader.bind();

        shader.setUniform("uModelProj", transformation.getFinalMat(model, new Vector3d(Camera.main.getPosition().x, Camera.main.getPosition().y, 0.0), Camera.getProjectionMatrix()));
        shader.setUniform("uTime", (float)Time.time());

        mesh.bind();

        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        mesh.unbind();

        shader.unbind();
    }

    public static void DrawSprite(Texture texture, Transformation transformation, Shader shader) {
        Objects.requireNonNull(shader);

        InitSpriteMesh();

        shader.setUniform("uMainTex", texture);
        DrawMesh(spriteMesh, transformation, shader);
    }

    public static void DrawSprite(Transformation transformation, TextureMaterial material) {
        InitSpriteMesh();

        DrawMesh(spriteMesh, transformation, material);
    }

    public static void DrawSprite(Texture texture, Transformation transformation, Material shader) {
        Objects.requireNonNull(shader);

        shader.Set();

        DrawSprite(texture, transformation, shader.getShader());
    }

    private static void InitSpriteMesh() {
        if (spriteMesh == null) {
            spriteMesh = new Mesh();

            spriteMesh.vertices(new Vector3f[]{
                    new Vector3f(-0.5f, -0.5f, 0.f),
                    new Vector3f(-0.5f, 0.5f, 0.f),
                    new Vector3f(0.5f, -0.5f, 0.f),
                    new Vector3f(.5f, .5f, 0.f)
            });

            spriteMesh.triangles(new int[] {0,2,1,2,3,1});
        }
    }
}
