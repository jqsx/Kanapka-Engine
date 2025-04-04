package KanapkaEngine.Net.DataStorage.Standard;

import KanapkaEngine.Net.DataStorage.Payload;

import java.nio.ByteBuffer;

public final class IntPayload implements Payload<Integer> {
    private int value = 0;

    @Override
    public void buffer(ByteBuffer buffer) {
        buffer.putInt(value);
    }

    @Override
    public void value(ByteBuffer buffer) {
        value = buffer.getInt();
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void set(Integer v) {
        this.value = v;
    }

    @Override
    public int getSize() {
        return Integer.BYTES;
    }
}
