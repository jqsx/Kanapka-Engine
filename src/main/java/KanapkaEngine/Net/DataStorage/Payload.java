package KanapkaEngine.Net.DataStorage;

import java.nio.ByteBuffer;

public interface Payload<T> {
    void buffer(ByteBuffer buffer);
    void value(ByteBuffer buffer);

    T get();
    void set(T v);

    int getSize();
}
