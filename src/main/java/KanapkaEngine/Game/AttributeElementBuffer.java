package KanapkaEngine.Game;

import KanapkaEngine.Editor.Attributes.Serialized;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class AttributeElementBuffer extends AttributeBuffer {
    private static final Logger logger = new Logger("AttributeElementBuffer");
    private final int VAO;
    private final int EBO;

    private int _attribCount = 0;

    private final HashMap<String, AttribLocationData> AttributeBuffers = new HashMap<>();

    private boolean isDisposed = false;

    private int vertexCount = 0;

    private boolean hasInstancedAttributes = false;

    private int instanceCount = 0;

    public AttributeElementBuffer() {
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

    @Serialized
    public void logInstanceCount() {
        logger.log("Instance count: " + instanceCount);
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

        logger.log("Created attrib location " + identifier);

        _attribCount++;
        return attrib_location;
    }

    public int createAttributeMat3(String identifier) {
        Objects.requireNonNull(identifier);
        if (identifier.isEmpty())
            throw new RuntimeException("Attribute identifier cannot be empty.");

        int out = createAttribute(identifier + "0");
        createAttribute(identifier + "1");
        createAttribute(identifier + "2");

        return out;
    }

    public int createAttributeMat4(String identifier) {
        Objects.requireNonNull(identifier);
        if (identifier.isEmpty())
            throw new RuntimeException("Attribute identifier cannot be empty.");

        int out = createAttribute(identifier + "0");
        createAttribute(identifier + "1");
        createAttribute(identifier + "2");
        createAttribute(identifier + "3");

        return out;
    }

    public void BufferFloats(String identifier, float... floats) {
        _bufferFloatArray(identifier, floats, 1);
    }

    public void BufferVec2(String identifier, Vector2f... vector2fs) {
        float[] floats = new float[vector2fs.length * 2];

        for (int index = 0; index < vector2fs.length; index++) {
            floats[index*2] = vector2fs[index].x;
            floats[index*2+1] = vector2fs[index].y;
        }

        _bufferFloatArray(identifier, floats, 2);
    }

    public void BufferVec3(String identifier, Vector3f... vector3fs) {
        float[] floats = new float[vector3fs.length * 3];

        for (int index = 0; index < vector3fs.length; index++) {
            floats[index*3] = vector3fs[index].x;
            floats[index*3+1] = vector3fs[index].y;
            floats[index*3+2] = vector3fs[index].z;
        }

        _bufferFloatArray(identifier, floats, 3);
    }

    public void BufferVec4(String identifier, Vector4f... vector4fs) {
        float[] floats = new float[vector4fs.length * 4];

        for (int index = 0; index < vector4fs.length; index++) {
            floats[index*4] = vector4fs[index].x;
            floats[index*4+1] = vector4fs[index].y;
            floats[index*4+2] = vector4fs[index].z;
            floats[index*4+3] = vector4fs[index].w;
        }

        _bufferFloatArray(identifier, floats, 4);
    }

    private void _bufferFloatsInstanced(String identifier, float[] floats, int size) {
        if (!AttributeBuffers.containsKey(identifier))
            throw new RuntimeException("Unidentified identifier key " + identifier);

        hasInstancedAttributes = true;

        AttribLocationData data = AttributeBuffers.get(identifier);

        bind();

        glBindBuffer(GL_ARRAY_BUFFER, data.VBO);
        glBufferData(GL_ARRAY_BUFFER, floats, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(data.AttribLocation);
        glVertexAttribPointer(data.AttribLocation, size, GL_FLOAT, false, 0, 0);

        glVertexAttribDivisor(data.AttribLocation, 1);

        instanceCount = floats.length / size;

        unbind();
    }

    public void BufferFloatsInstanced(String identifier, float[] floats) {
        _bufferFloatsInstanced(identifier, floats, 1);
    }

    public void BufferFloatsInstancedc(String identifier, float[] floats, int size) {
        _bufferFloatsInstanced(identifier, floats, size);
    }

    public void BufferVec2Instanced(String identifier, Vector2f[] vector2fs) {
        float[] floats = new float[vector2fs.length * 2];

        for (int index = 0; index < vector2fs.length; index++) {
            floats[index*2] = vector2fs[index].x;
            floats[index*2+1] = vector2fs[index].y;
        }

        _bufferFloatsInstanced(identifier, floats, 2);
    }

    private void _bufferFloatArray(String identifier, float[] floats, int size) {
        if (!AttributeBuffers.containsKey(identifier))
            throw new RuntimeException("Unidentified identifier key " + identifier);

        AttribLocationData data = AttributeBuffers.get(identifier);

        bind();

        glBindBuffer(GL_ARRAY_BUFFER, data.VBO);
        glBufferData(GL_ARRAY_BUFFER, floats, GL_STATIC_DRAW);

        glVertexAttribPointer(data.AttribLocation, size, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(data.AttribLocation);

        unbind();
    }

    public void BufferTriangles(int... triangles) {
        bind();

        vertexCount = triangles.length;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.callocInt(triangles.length);

            buffer.put(triangles);

            buffer.flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }

        unbind();
    }

    @Override
    public void bind() {
        if (isDisposed) {
            throw new RuntimeException("Attempt at binding a disposed vertex array.");
        }
        glBindVertexArray(VAO);
    }

    @Override
    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    protected void Dispose(boolean removeFromLoaded) {
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

    public boolean isInstanced() {
        return hasInstancedAttributes;
    }

    public int getInstanceCount() {
        return instanceCount;
    }
}
