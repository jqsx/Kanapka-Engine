package KanapkaEngine.Game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL30.*;
public final class AttributeBuffer {
    static final List<AttributeBuffer> LoadedAttributeBuffers = new ArrayList<>();

    private final int VAO;
    private final int EBO;

    private int _attribCount = 0;

    private final HashMap<String, AttribLocationData> AttributeBuffers = new HashMap<>();

    private boolean isDisposed = false;

    private int vertexCount = 0;

    public AttributeBuffer() {
        int[] vaoptr = new int[1];
        glGenVertexArrays(vaoptr);

        this.VAO = vaoptr[0];

        if (VAO == 0) {
            throw new RuntimeException("Failed to create a new vertex array object.");
        }

        int[] eboptr = new int[1];

        glGenBuffers(eboptr);

        this.EBO = eboptr[0];

        if (EBO == 0){
            glDeleteVertexArrays(VAO);
            throw new RuntimeException("Failed to create a new element buffer object.");
        }

        LoadedAttributeBuffers.add(this);
    }

    /**
     *
     * @param identifier String used to identify the buffer array location
     * @return The location of the buffer object.
     */
    public int createAttribute(String identifier) {
        Objects.requireNonNull(identifier);
        if (identifier.isEmpty())
            throw new RuntimeException("Attribute identifier cannot be empty.");

        int[] vboptr = new int[1];

        glGenBuffers(vboptr);

        int attrib_location = _attribCount;

        AttributeBuffers.put(identifier, new AttribLocationData(vboptr[0], attrib_location));
        _attribCount++;
        return attrib_location;
    }

    public void BufferFloats(String identifier, float[] floats) {
        _bufferFloatArray(identifier, floats, 1);
    }

    public void BufferVec2(String identifier, Vector2f[] vector2fs) {
        float[] floats = new float[vector2fs.length * 2];

        for (int index = 0; index < vector2fs.length; index++) {
            floats[index*2] = vector2fs[index].x;
            floats[index*2+1] = vector2fs[index].y;
        }

        _bufferFloatArray(identifier, floats, 2);
    }

    public void BufferVec3(String identifier, Vector3f[] vector3fs) {
        float[] floats = new float[vector3fs.length * 3];

        for (int index = 0; index < vector3fs.length; index++) {
            floats[index*3] = vector3fs[index].x;
            floats[index*3+1] = vector3fs[index].y;
            floats[index*3+2] = vector3fs[index].z;
        }

        _bufferFloatArray(identifier, floats, 3);
    }

    public void BufferVec4(String identifier, Vector4f[] vector4fs) {
        float[] floats = new float[vector4fs.length * 4];

        for (int index = 0; index < vector4fs.length; index++) {
            floats[index*4] = vector4fs[index].x;
            floats[index*4+1] = vector4fs[index].y;
            floats[index*4+2] = vector4fs[index].z;
            floats[index*4+3] = vector4fs[index].w;
        }

        _bufferFloatArray(identifier, floats, 4);
    }

    private void _bufferFloatArray(String identifier, float[] floats, int size) {
        if (!AttributeBuffers.containsKey(identifier))
            throw new RuntimeException("Unidentified identifier key " + identifier);

        AttribLocationData data = AttributeBuffers.get(identifier);

        bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.callocFloat(floats.length);

            buffer.put(0, floats);

            glBindBuffer(GL_ARRAY_BUFFER, data.VBO);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

            glVertexAttribPointer(data.AttribLocation, size, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(data.AttribLocation);
        }

        unbind();
    }

    public void BufferTriangles(int[] triangles) {
        bind();

        vertexCount = triangles.length;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.callocInt(triangles.length);

            buffer.put(0, triangles);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }

        unbind();
    }

    public void bind() {
        if (isDisposed) {
            throw new RuntimeException("Attempt at binding a disposed vertex array.");
        }
        glBindVertexArray(VAO);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    private static class AttribLocationData {
        public final int VBO;
        public final int AttribLocation;

        public AttribLocationData(int VBO, int AttribLocation) {
            this.VBO = VBO;
            this.AttribLocation = AttribLocation;
        }
    }

    void Dispose(boolean removeFromLoaded) {
        unbind();
        for (AttribLocationData data : AttributeBuffers.values()) {
            glDeleteBuffers(data.VBO);
        }

        glDeleteBuffers(EBO);

        glDeleteVertexArrays(VAO);

        if (removeFromLoaded)
            LoadedAttributeBuffers.remove(this);

        isDisposed = true;
    }

    public void Dispose() {
        Dispose(true);
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
