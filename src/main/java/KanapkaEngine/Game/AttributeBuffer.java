package KanapkaEngine.Game;

import java.util.ArrayList;
import java.util.List;

abstract class AttributeBuffer {
    protected final static List<AttributeBuffer> LoadedAttributeBuffers = new ArrayList<>();

    protected abstract void Dispose(boolean value);

    public abstract void bind();
    public abstract void unbind();
}
