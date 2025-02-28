package KanapkaEngine.Game;

import KanapkaEngine.Components.ResourceLoader;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final static Matrix4f model = new Matrix4f().identity();
    private static final Logger logger = new Logger("SHADER");
    static final HashMap<String, Shader> LoadedShaders = new HashMap<>();

    private final int programId;

    private final HashMap<String, Integer> UniformLocations = new HashMap<>();

    private boolean isDisposed = false;

    private static float[] preallocatedMat4Floats = new float[16];

    public Shader(String id, String fragCode, String vertCode) {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }
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

            int count = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                for (int i = 0; i < count; i++) {
                    IntBuffer size = stack.callocInt(1);
                    IntBuffer type = stack.callocInt(1);

                    String name = glGetActiveUniform(programId, i, size, type);

                    createUniform(name);
                }
            }

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
            String type = shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT";
            throw new Exception("Error compiling " + type + "  Shader code: " + glGetShaderInfoLog(shaderId, 1024));
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
            mat.get(preallocatedMat4Floats);
            bind();
            glUniformMatrix4fv(location, false, preallocatedMat4Floats);
            unbind();
        }
    }

    public final void setUniform(String uniform, float value) {
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        bind();
        glUniform1f(location, value);
        unbind();
    }
    public final void setUniform(String uniform, int value) {
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        bind();
        glUniform1i(location, value);
        unbind();
    }

    public final void setUniform(String uniform, Vector2f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        bind();
        glUniform2f(location, value.x, value.y);
        unbind();
    }

    public final void setUniform(String uniform, Vector3f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        bind();
        glUniform3f(location, value.x, value.y, value.z);
        unbind();
    }

    public final void setUniform(String uniform, Vector4f value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        int location = UniformLocations.get(uniform);

        bind();
        glUniform4f(location, value.x, value.y, value.z, value.w);
        unbind();
    }

    public final void setUniformArray(String uniform, Vector4f[] value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        bind();

//        for (int index = 0; index < value.length; index++) {
//            String uniform_index = uniform + "[" + index + "]";
//
//            Vector4f v = value[index];
//
//            glUniform4f(glGetUniformLocation(programId, uniform_index), v.x, v.y, v.z, v.w);
//        }

        float[] _v = new float[value.length * 4];

        for (int i = 0; i < value.length; i++) {
            _v[i*4] = value[i].x;
            _v[i*4+1] = value[i].y;
            _v[i*4+2] = value[i].z;
            _v[i*4+3] = value[i].w;
        }

        glUniform4fv(UniformLocations.get(uniform), _v);

        unbind();
    }

    public final void setUniform(String uniform, Texture texture) {
        setUniform(uniform, texture, GL_TEXTURE0, 0);
    }

    public final void setUniform(String uniform, Texture texture, int gltexture, int i) {
        Objects.requireNonNull(texture);
        Objects.requireNonNull(uniform);
        if (isDisposed)
            return;
        if (!UniformLocations.containsKey(uniform))
            return;

        bind();

        setUniform(uniform, i);

        glActiveTexture(gltexture);

        glBindTexture(GL_TEXTURE_2D, texture.textureId);

        unbind();
    }

    /**
     * Shorthand to setting a mat4 via the Transformation object.
     * @param uniform
     * @param transformation
     */
    public final void setUniform(String uniform, Transformation transformation) {
        setUniform(uniform, transformation.getFinalMat(model, new Vector3d(Camera.main.getPosition().x, Camera.main.getPosition().y, 0.0), Camera.getProjectionMatrix()));
    }

    public final void bind() {
        if (isDisposed)
            return;
        glUseProgram(programId);
    }

    public final void unbind() {
        glUseProgram(0);
    }

    public final void Dispose() {
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

        String fragCode = ResourceLoader.loadStringFromResource(path + ".frag");
        String vertCode = ResourceLoader.loadStringFromResource(path + ".vert");

        return new Shader(id, fragCode, vertCode);
    }

    public static Shader findOrCreate(String id, String path) {
        Shader shader = Shader.find(id);

        if (shader == null)
            shader = Shader.createShaderFromResources(id, path);

        return shader;
    }

    public final static class Standard {

        private static Shader standard;
        private static Shader texture;

        public static Shader getStandardShader() {
            return standard;
        }

        public static Shader getTextureShader() {
            return texture;
        }

        protected static void init() {
            texture = Shader.findOrCreate("builtIn:texture", "Shader/standard/texture");
            standard = Shader.findOrCreate("builtIn:standard", "Shader/standard/standard");
        }
    }
}
