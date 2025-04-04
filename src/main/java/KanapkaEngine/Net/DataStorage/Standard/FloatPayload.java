package KanapkaEngine.Net.DataStorage.Standard;

import KanapkaEngine.Net.DataStorage.Payload;

import java.nio.ByteBuffer;

public final class FloatPayload implements Payload<Float> {
    private float value;
    @Override
    public void buffer(ByteBuffer buffer) {
        buffer.putFloat(value);
    }

    @Override
    public void value(ByteBuffer buffer) {
        value = buffer.getFloat();
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public void set(Float v) {
        this.value = v;
    }

    @Override
    public int getSize() {
        return Float.BYTES;
    }
}
