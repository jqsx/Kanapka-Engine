package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkServer;

import java.awt.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Route {
    protected boolean isReady = false;
    private short id;

    public Route() {
        RouteManager.defineRoute(this);
    }

    public final short getID() {
        return id;
    }

    /**
     * Server Side
     * @param conn
     * @param data
     */
    public void ServerClient_IN(NetworkConnectionToClient conn, byte[] data) {

    }

    /**
     * Client Side
     * @param data
     */
    public void Client_IN(byte[] data) {

    }

    public void onServerStart() {

    }

    public void onServerStop() {

    }

    public void onClientConnect(InetAddress address) {

    }

    public void onClientDisconnect() {

    }

    public void onServerClientDisconnect(NetworkConnectionToClient conn) {

    }

    public void onServerClientConnect(NetworkConnectionToClient conn) {

    }

    public final void sendToClient(NetworkConnectionToClient connectionToClient, byte[] data) {
        if (NetworkServer.clients.contains(connectionToClient))
            connectionToClient.send(getID(), data);
        else
            System.out.println("[MSG>SERVERCLIENT] Server client isn't registered in current context.");
    }

    public final void sendToServer(byte[] data) {
        if (NetworkClient.isConnected())
            NetworkClient.send(getID(), data);
        else
            System.out.println("[MSG>SERVER] Client isn't connected to a server.");
    }

    protected final void define(short id) {
        this.id = id;
    }

    /**
     * Converts object's public fields into a byte stream. <br>
     * Syncable classes: int, char, byte, short, float, double, String. <br>
     * <strong>Recommend using ByteBuffers and pre-allocating bytes without this method</strong>
     * @param object
     * @return
     */
    public static byte[] convert(Object object) {
        LinkedList<Byte> buffer = new LinkedList<>();
        int len = 0;
        for (Field field : object.getClass().getFields()) {
            Class<?> c = field.getType();
            ByteBuffer temp;
            try {
                if (c == int.class) {
                    temp = ByteBuffer.allocate(Integer.BYTES);
                    Integer v = (Integer) field.get(object);

                    temp.putInt(v);

                    for (byte b : temp.array())
                        buffer.add(b);

                    len += Integer.BYTES;
                }
                else if (c == char.class) {
                    temp = ByteBuffer.allocate(Character.BYTES);
                    Character v = (Character) field.get(object);

                    temp.putChar(v);

                    for (byte b : temp.array())
                        buffer.add(b);

                    len += Character.BYTES;
                }
                else if (c == byte.class) {
                    Byte b = (Byte) field.get(object);

                    buffer.add(b);

                    len += Byte.BYTES;
                }
                else if (c == short.class) {
                    temp = ByteBuffer.allocate(Short.BYTES);
                    Short v = (Short) field.get(object);

                    temp.putShort(v);

                    for (byte b : temp.array())
                        buffer.add(b);

                    len += Short.BYTES;
                }
                else if (c == float.class) {
                    temp = ByteBuffer.allocate(Float.BYTES);
                    Float v = (Float) field.get(object);

                    temp.putFloat(v);

                    for (byte b : temp.array())
                        buffer.add(b);

                    len += Float.BYTES;
                }
                else if (c == double.class) {
                    temp = ByteBuffer.allocate(Double.BYTES);
                    Double v = (Double) field.get(object);

                    temp.putDouble(v);

                    for (byte b : temp.array())
                        buffer.add(b);

                    len += Double.BYTES;
                }
                else if (c == String.class) {
                    String v = (String) field.get(object);

                    temp = ByteBuffer.allocate(Integer.BYTES);

                    temp.putInt(v.length());

                    for (byte b : temp.array())
                        buffer.add(b);

                    for (byte b : v.getBytes())
                        buffer.add(b);

                    len += Integer.BYTES + v.length() * Character.BYTES;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        byte[] out = new byte[len];

        int i = 0;
        for (byte b : buffer) {
            out[i] = b;
            i++;
        }

        return out;
    }

    /**
     * Requires class to be a public class with a public constructor.
     * @param data
     * @param cast
     * @return
     * @param <T>
     */
    public static <T> T revert(byte[] data, Class<T> cast) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        T target = null;
        try {
            target = cast.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Field field : cast.getFields()) {
            Class<?> c = field.getType();
            try {
                if (c == int.class) {
                    Integer v = buffer.getInt();

                    field.set(target, v);
                }
                else if (c == char.class) {
                    Character v = buffer.getChar();

                    field.set(target, v);
                }
                else if (c == byte.class) {
                    Byte v = buffer.get();

                    field.set(target, v);
                }
                else if (c == short.class) {
                    Short v = buffer.getShort();

                    field.set(target, v);
                }
                else if (c == float.class) {
                    Float v = buffer.getFloat();

                    field.set(target, v);
                }
                else if (c == double.class) {
                    Double v = buffer.getDouble();

                    field.set(target, v);
                }
                else if (c == String.class) {
                    int len = buffer.getInt();

                    byte[] v = new byte[len];

                    buffer.get(v);

                    field.set(target, new String(v));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return target;
    }
}
