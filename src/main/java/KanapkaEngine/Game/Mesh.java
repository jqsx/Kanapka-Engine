package KanapkaEngine.Game;

import org.joml.Vector3f;

public final class Mesh {
    final AttributeElementBuffer attributeBuffer;

    private static final String VERTICE_ID = "vertices";

    public Mesh() {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }
        attributeBuffer = new AttributeElementBuffer();

        attributeBuffer.createAttribute(VERTICE_ID);
    }

    public void triangles(int[] triangles) {
        attributeBuffer.BufferTriangles(triangles);
    }

    public void vertices(Vector3f[] vertices) {
        attributeBuffer.BufferVec3(VERTICE_ID, vertices);
    }

    /**
     *  The Mesh class uses the AttributeBuffer class in order to buffer meshes, it is disposed of automatically.
     */
    public void Dispose() {
        attributeBuffer.Dispose();
    }
}
