package KanapkaEngine.Game;

import org.joml.Vector3f;

public final class Mesh {
    final AttributeBuffer attributeBuffer;

    private static final String VERTICE_ID = "vertices";

    public Mesh() {
        attributeBuffer = new AttributeBuffer();

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
