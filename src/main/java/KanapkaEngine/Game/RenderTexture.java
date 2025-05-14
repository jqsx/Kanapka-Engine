package KanapkaEngine.Game;

import java.util.Vector;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

@WIP
public final class RenderTexture {
    private static final int[] EMPTY_PTR = new int[0];

    private static final Logger logger = new Logger("rendertexture");

    protected static final Vector<RenderTexture> RENDER_TEXTURES = new Vector<>();

    private static final int[] attachments = new int[] { GL_COLOR_ATTACHMENT0 };

    final int frameBuffer;

    final int textureId;

    final int renderBuffer;

    private int width;
    private int height;

    public RenderTexture(int width, int height) {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }

        this.width = width;
        this.height = height;

        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        // 2. Create Texture to Render Into
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Attach texture to framebuffer
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        // 3. Optional: Create and attach Renderbuffer for Depth+Stencil
        renderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        int code = GL_FRAMEBUFFER_COMPLETE;
        if((code = glCheckFramebufferStatus(GL_FRAMEBUFFER)) != GL_FRAMEBUFFER_COMPLETE) {
            logger.error("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");

            if (code == GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
                logger.error("Incomplete attachment.");
            else if (code == GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT)
                logger.error("Incomplete missing attachment.");
            else if (code == GL_FRAMEBUFFER_UNDEFINED) {
                logger.error("Undefined.");
            }
            else if (code == GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
                logger.error("Incomplete draw buffer.");
            }
            else if (code == GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
                logger.error("Incomplete read buffer.");
            }
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        RENDER_TEXTURES.add(this);
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    private void bindTex() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    private void bindRenBuffer() {
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
    }

    private void unbindRenBuffer() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    private void unbindTex() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void DrawFullScreen(Shader shader) {
        Graphics.DrawRenderTexture(this, shader);
    }

    public void clear() {

    }

    public void Dispose() {
        Dispose(true);
    }

    protected void Dispose(boolean fromList) {
        glDeleteTextures(textureId);
        glDeleteFramebuffers(frameBuffer);
        glDeleteRenderbuffers(renderBuffer);

        if (fromList) {
            RENDER_TEXTURES.remove(this);
        }
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Engine.getMainInstance().getWindow().getWidth(), Engine.getMainInstance().getWindow().getHeight());
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
