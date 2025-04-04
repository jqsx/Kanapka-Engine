package KanapkaEngine.Net.DataStorage.Standard;

import KanapkaEngine.Net.DataStorage.Payload;
import org.joml.Vector2d;

import java.nio.ByteBuffer;

public final class Vector2dPayload implements Payload<Vector2d> {
    private Vector2d value;

    @Override
    public void buffer(ByteBuffer buffer) {
        buffer.putDouble(value.x);
        buffer.putDouble(value.y);
    }

    @Override
    public void value(ByteBuffer buffer) {
        value.set(buffer.getDouble(), buffer.getDouble());
    }

    @Override
    public Vector2d get() {
        return value;
    }

    @Override
    public void set(Vector2d v) {
        value.set(v);
    }

    @Override
    public int getSize() {
        return Double.BYTES * 2;
    }
}
