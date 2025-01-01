package KanapkaEngine.Game;

import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.*;

public class AttributeArrayBuffer extends AttributeBuffer {

    private final int VAO;
    private final int EBO;

    private int _attribCount = 0;

    private final HashMap<String, AttribLocationData> AttributeBuffers = new HashMap<>();

    private boolean isDisposed = false;

    private int vertexCount = 0;

    private boolean hasInstancedAttributes = false;

    private int instanceCount = 0;

    public AttributeArrayBuffer() {
        this.VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        this.EBO = glGenBuffers();
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
    protected void Dispose(boolean value) {

    }
}
