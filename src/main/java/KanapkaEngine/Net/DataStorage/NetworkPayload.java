package KanapkaEngine.Net.DataStorage;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NetworkPayload {

    /**
     * I cba to look for a data structure that doesn't automatically sort
     */
    private LinkedList<Payload> filteredParameters;

    private void Locate() {
        if (filteredParameters == null)
            filteredParameters = new LinkedList<>();
        else return;

        for (Field field : this.getClass().getFields()) {
            if (field.getType().isAssignableFrom(Payload.class)) {
                try {
                    Payload payload = (Payload) field.get(this);

                    if (!Modifier.isFinal(field.getModifiers()))
                        return;

                    filteredParameters.add(payload);
                } catch (IllegalAccessException e) {

                }
            }
        }
    }

    public final ByteBuffer collect() {
        Locate();

        int size = 0;

        for (Payload payload : filteredParameters) {
            size += payload.getSize();
        }

        ByteBuffer buffer = ByteBuffer.allocate(size);

        for (Payload payload : filteredParameters) {
            payload.buffer(buffer);
        }

        return buffer;
    }

    public final void read(ByteBuffer buffer) {
        Locate();

        for (Payload payload : filteredParameters) {
            payload.value(buffer);
        }
    }
}
