package KanapkaEngine.Game;

import KanapkaEngine.Components.ResourceLoader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private static final Logger logger = new Logger("SHADER");
    static final HashMap<String, Shader> LoadedShaders = new HashMap<>();

    private final int programId;

    private final HashMap<String, Integer> UniformLocations = new HashMap<>();

    private boolean isDisposed = false;

    public Shader(String id, String fragCode, String vertCode) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(fragCode);
        Objects.requireNonNull(vertCode);

        if (LoadedShaders.containsKey(id))
            throw new RuntimeException("Shader with this id has already been defined.");

        LoadedShaders.put(id, this);

        this.programId = glCreateProgram();

        try {
            int fragmentShaderId = createShader(fragCode, GL_FRAGMENT_SHADER);
            int vertexShaderId = createShader(vertCode, GL_VERTEX_SHADER);

            link(vertexShaderId, fragmentShaderId);

            createUniform("uModelProj", false);
            createUniform("uTime", false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private void link(int vert, int frag) throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (frag != 0) {
            glDetachShader(programId, frag);
        }
        if (vert != 0) {
            glDetachShader(programId, vert);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            logger.warn("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        glDeleteShader(frag);
        glDeleteShader(vert);
    }

    public final void createUniform(String uniform) {
        createUniform(uniform, true);
    }

    protected final void createUniform(String uniform, boolean verbose) {
        Objects.requireNonNull(uniform);
        if (UniformLocations.containsKey(uniform))
            return;

        if (isDisposed)
            return;

        int location = glGetUniformLocation(programId, uniform);

        if (location == -1) {
            if (verbose)
                logger.warn("Uniform not found in shader. [ " + uniform + " ]");
            return;
        }

        UniformLocations.put(uniform, location);
    }

    public final void setUniform(String uniform, Matrix4f mat) {
        Objects.requireNonNull(mat);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            mat.get(fb);
            glUniformMatrix4fv(location, false, fb);
        }
    }

    public final void setUniform(String uniform, float value) {
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        glUniform1f(location, value);
    }

    public final void setUniform(String uniform, Vector2f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        glUniform2f(location, value.x, value.y);
    }

    public final void setUniform(String uniform, Vector3f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        glUniform3f(location, value.x, value.y, value.z);
    }

    public final void setUniform(String uniform, Vector4f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public final void setUniform(String uniform, Texture texture) {
        Objects.requireNonNull(texture);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        bind();

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(programId, texture.textureId);

        unbind();
    }

    public void bind() {
        if (isDisposed)
            return;
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    void Dispose() {
        if (isDisposed)
            return;
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
            isDisposed = true;
        }
    }

    public static Shader find(String id) {
        return LoadedShaders.get(id);
    }

    public static Shader createShaderFromResources(String id, String path) {
        if (LoadedShaders.containsKey(id)) {
            logger.error("A shader with this id has already been registered.");
            return null;
        }

        String fragCode = ResourceLoader.loadStringFromFile(path + ".frag");
        String vertCode = ResourceLoader.loadStringFromFile(path + ".vert");

        return new Shader(id, fragCode, vertCode);
    }

    public static Shader findOrCreate(String id, String path) {
        Shader shader = Shader.find(id);

        if (shader == null)
            shader = Shader.createShaderFromResources(id, path);

        return shader;
    }
}
