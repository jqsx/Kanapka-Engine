package KanapkaEngine.Game;

import KanapkaEngine.Components.Material;
import KanapkaEngine.Components.TextureMaterial;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/*
I left this class as not final so that you can write your own custom graphics implementations
 */
public class Graphics {

    private static final Logger logger = new Logger("Graphics");
    private final static Matrix4f model = new Matrix4f().identity();

    public final static Vector3f WIREFRAME_COLOR = new Vector3f(1.f, 0.f, 1.f);

    private final static Transformation transformation = new Transformation(new Vector3d(), new Vector3d(), new Vector3f());

    private static Mesh spriteMesh;

    private static Mesh renderPassMesh;

    public static void DrawMesh(Mesh mesh, Transformation transformation, Shader shader) {
        DrawMesh(mesh.attributeBuffer, transformation, shader);
    }

    public static void DrawMesh(Mesh mesh, Transformation transformation, Material shader) {
        Objects.requireNonNull(shader);

        shader.Set();

        DrawMesh(mesh.attributeBuffer, transformation, shader.getShader());
    }

    public static void DrawWireframe(AttributeElementBuffer mesh, Transformation transformation, Shader shader) {
        Objects.requireNonNull(shader);
        Objects.requireNonNull(mesh);
        Objects.requireNonNull(transformation);
        if (Camera.main == null)
            return;

        shader.bind();

        shader.setUniform("uModelProj", transformation.getFinalMat(model, new Vector3d(Camera.main.getPosition().x, Camera.main.getPosition().y, 0.0), Camera.getProjectionMatrix()));
        shader.setUniform("uTime", (float)Time.time());
        shader.setUniform("uScreenWidth", Engine.getMainInstance().getWindow().getWidth());
        shader.setUniform("uScreenHeight", Engine.getMainInstance().getWindow().getHeight());
        shader.setUniform("uColor", WIREFRAME_COLOR);

        shader.bind();
        mesh.bind();

        glDrawElements(GL_LINE_LOOP, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        mesh.unbind();

        shader.unbind();
    }

    public static void DrawWireframe(Transformation transformation) {
        DrawWireframe(spriteMesh.attributeBuffer, transformation, Shader.Standard.getStandardShader());
    }

    public static void DrawWireframe(double x, double y, double width, double height, double rotation) {
        transformation.position.set(x, y, 0);
        transformation.rotation.set(0, 0, rotation);
        transformation.scale.set(width, height, 1.0);

        DrawWireframe(transformation);
    }

    public static void DrawWireframe(Vector2d p, Vector2d s, double r) {
        DrawWireframe(p.x, p.y, s.x, s.y, r);
    }

    public static void DrawWireframe(Vector2d p, Vector2d s) {
        DrawWireframe(p, s, 0.0);
    }

    public static void DrawMesh(AttributeElementBuffer mesh, Transformation transformation, Shader shader) {
        Objects.requireNonNull(shader);
        Objects.requireNonNull(mesh);
        Objects.requireNonNull(transformation);
        if (Camera.main == null)
            return;

        shader.bind();

        shader.setUniform("uModelProj", transformation.getFinalMat(model, new Vector3d(Camera.main.getPosition().x, Camera.main.getPosition().y, 0.0), Camera.getProjectionMatrix()));
        shader.setUniform("uTime", (float)Time.time());
        shader.setUniform("uScreenWidth", Engine.getMainInstance().getWindow().getWidth());
        shader.setUniform("uScreenHeight", Engine.getMainInstance().getWindow().getHeight());

        shader.bind();
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

    public static void DrawRenderTexture(RenderTexture texture, Material material) {
        Objects.requireNonNull(material);

        material.Set();

        DrawRenderTexture(texture, material.getShader());
    }

    public static void DrawRenderTexture(RenderTexture texture, Shader shader) {
        Objects.requireNonNull(texture);
        Objects.requireNonNull(shader);

        if (Camera.main == null)
            return;

        InitRenderTextureMesh();

        shader.setUniform("uMainTex", texture.getTexture());
        shader.setUniform("uTime", (float)Time.time());
        shader.setUniform("uScreenWidth", Engine.getMainInstance().getWindow().getWidth());
        shader.setUniform("uScreenHeight", Engine.getMainInstance().getWindow().getHeight());

        shader.bind();

        renderPassMesh.attributeBuffer.bind();

        glDrawElements(GL_TRIANGLES, renderPassMesh.attributeBuffer.getVertexCount(), GL_UNSIGNED_INT, 0);

        renderPassMesh.attributeBuffer.unbind();

        shader.unbind();
    }

    public static void DrawFullScreen(Texture texture, Shader shader) {
        Objects.requireNonNull(texture);
        Objects.requireNonNull(shader);

        if (Camera.main == null)
            return;

        InitRenderTextureMesh();

        shader.setUniform("uMainTex", texture);
        shader.setUniform("uTime", (float)Time.time());
        shader.setUniform("uScreenWidth", Engine.getMainInstance().getWindow().getWidth());
        shader.setUniform("uScreenHeight", Engine.getMainInstance().getWindow().getHeight());

        shader.bind();

        renderPassMesh.attributeBuffer.bind();

        glDrawElements(GL_TRIANGLES, renderPassMesh.attributeBuffer.getVertexCount(), GL_UNSIGNED_INT, 0);

        renderPassMesh.attributeBuffer.unbind();

        shader.unbind();
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

    public static void DrawSprite(Texture texture, Transformation transformation) {
        DrawSprite(texture, transformation, Shader.Standard.getTextureShader());
    }

    public static void DrawSprite(Texture texture, Vector2d position, Vector2d scale, float rotation) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(scale);
        transformation.Update(position, scale, rotation);

        DrawSprite(texture, transformation);
    }

    public static void DrawInstancedSprite(Shader shader, Texture texture, Transformation transformation, AttributeElementBuffer buffer) {
        Objects.requireNonNull(texture);
        Objects.requireNonNull(buffer);
        Objects.requireNonNull(transformation);

        if (!buffer.isInstanced()) {
            logger.warn("Rendering non instanced attribute buffer");
            return;
        }

        if (Camera.main == null)
            return;

        shader.setUniform("uModelProj", transformation.getFinalMat(model, new Vector3d(Camera.main.getPosition().x, Camera.main.getPosition().y, 0.0), Camera.getProjectionMatrix()));
        shader.setUniform("uTime", (float)Time.time());
        shader.setUniform("uScreenWidth", Engine.getMainInstance().getWindow().getWidth());
        shader.setUniform("uScreenHeight", Engine.getMainInstance().getWindow().getHeight());
        shader.setUniform("uMainTex", texture);

        shader.bind();
        buffer.bind();

        //glDrawElements(GL_TRIANGLES, buffer.getVertexCount(), GL_UNSIGNED_INT, 0);
        glDrawElementsInstanced(GL_TRIANGLES, buffer.getVertexCount(), GL_UNSIGNED_INT, 0, buffer.getInstanceCount());

        buffer.unbind();

        shader.unbind();
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

    private static void InitRenderTextureMesh() {
        if (renderPassMesh == null) {
            renderPassMesh = new Mesh();

            renderPassMesh.vertices(new Vector3f[]{
                    new Vector3f(-1f, -1f, 0.f),
                    new Vector3f(-1f, 1f, 0.f),
                    new Vector3f(1f, -1f, 0.f),
                    new Vector3f(1f, 1f, 0.f)
            });

            renderPassMesh.triangles(new int[] {0,2,1,2,3,1});
        }
    }
}
