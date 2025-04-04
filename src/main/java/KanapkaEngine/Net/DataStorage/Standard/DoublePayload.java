package KanapkaEngine.Net.DataStorage.Standard;

import KanapkaEngine.Net.DataStorage.Payload;

import java.nio.ByteBuffer;

public final class DoublePayload implements Payload<Double> {
    private double value;
    @Override
    public void buffer(ByteBuffer buffer) {
        buffer.putDouble(value);
    }

    @Override
    public void value(ByteBuffer buffer) {
        value = buffer.getDouble();
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void set(Double v) {
        this.value = v;
    }

    @Override
    public int getSize() {
        return Double.BYTES;
    }
}
