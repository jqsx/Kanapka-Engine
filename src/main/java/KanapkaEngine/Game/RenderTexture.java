package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public final class RenderTexture {
    private static final int[] EMPTY_PTR = new int[0];

    private static final Logger logger = new Logger("rendertexture");

    private static final int[] attachments = new int[] { GL_COLOR_ATTACHMENT0 };

    private final int frameBuffer;
    private final Texture target;

    private int width;
    private int height;

    public RenderTexture(int width, int height) {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }

        this.width = width;
        this.height = height;

        target = new Texture();

        target.setTextureUnsafe(width, height, null);

        frameBuffer = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, target.textureId, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            logger.error("Framebuffer is not complete!");
            throw new RuntimeException("Framebuffer not complete.");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        glViewport(0, 0, width, height);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        Window window = Engine.getMainInstance().getWindow();
        glViewport(0,0,window.getWidth(), window.getHeight());
    }

    public Texture getTexture() {
        return target;
    }
}
