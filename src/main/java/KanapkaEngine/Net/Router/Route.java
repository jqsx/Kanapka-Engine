package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetDataType;
import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;

import java.nio.ByteBuffer;

public class Route {
    protected boolean isReady = false;
    private short id;

    private NetDataType[] messageSize;

    public Route(NetDataType[] messageSize) {
        this.messageSize = messageSize;
    }

    public final short getID() {
        return id;
    }
    public final int getSize() {
        int i = 0;
        for (NetDataType dataType : messageSize) {
            i+=dataType.size;
        }
        return i;
    }

    /**
     * Server Side
     * @param connectionToClient
     * @param data
     */
    public void ServerClient_IN(NetworkConnectionToClient connectionToClient, byte[] data) {

    }

    /**
     * Client Side
     * @param data
     */
    public void Client_IN(byte[] data) {

    }

    public final void send(NetworkConnectionToClient connectionToClient, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(getSize());
        buffer.put(data);
        connectionToClient.send(getID(), buffer.array());
    }

    public final void send(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(getSize());
        buffer.put(data);
        System.out.println("Perpared message");
        NetworkClient.send(getID(), buffer.array());
        System.out.println("Sent Message");
    }

    protected final void define(short id) {
        this.id = id;
    }
}
