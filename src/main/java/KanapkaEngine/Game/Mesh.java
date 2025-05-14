package KanapkaEngine.Game;

import org.joml.Vector2f;
import org.joml.Vector3f;

public final class Mesh {
    final AttributeElementBuffer attributeBuffer;

    private static final String VERTICE_ID = "vertices";
    private static final String TEXCOORD_ID = "texcoord";

    public Mesh() {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }
        attributeBuffer = new AttributeElementBuffer();

        attributeBuffer.createAttribute(VERTICE_ID);
        attributeBuffer.createAttribute(TEXCOORD_ID);
    }

    public void triangles(int[] triangles) {
        attributeBuffer.BufferTriangles(triangles);
    }

    public void vertices(Vector3f[] vertices) {
        attributeBuffer.BufferVec3(VERTICE_ID, vertices);
    }

    public void uvs(Vector2f[] uvs) {
        attributeBuffer.BufferVec2(TEXCOORD_ID, uvs);
    }

    /**
     *  The Mesh class uses the AttributeBuffer class in order to buffer meshes, it is disposed of automatically.
     */
    public void Dispose() {
        attributeBuffer.Dispose();
    }
}
