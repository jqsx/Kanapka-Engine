package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public final class RenderTexture {
    private static final int[] EMPTY_PTR = new int[0];

    private static final Logger logger = new Logger("rendertexture");

    private static final int[] attachments = new int[] { GL_COLOR_ATTACHMENT0 };

    private final int frameBuffer;
    private final int renderBuffer;

    private final Texture target;

    public RenderTexture() {
        target = new Texture();

        Window window = Engine.getMainInstance().getWindow();

        target.setTextureUnsafe(1920, 1080, null);

        renderBuffer = glGenRenderbuffers();
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, 1980, 1080);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, target.textureId, 0);

        glDrawBuffers(attachments);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            logger.error("ERROR WHILE CREATING RENDERTEXTURE");
            return;
        }

        UpdateResolution();
    }

    private void UpdateResolution() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        Window window = Engine.getMainInstance().getWindow();

        glViewport(0,0,window.width, window.height);
    }

    public void bind() {
        UpdateResolution();

        glClear(GL_COLOR_BUFFER_BIT);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        Window window = Engine.getMainInstance().getWindow();
        glViewport(0,0,window.width, window.height);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        Window window = Engine.getMainInstance().getWindow();
        glViewport(0,0,window.width, window.height);
    }

    public Texture getTexture() {
        return target;
    }
}
