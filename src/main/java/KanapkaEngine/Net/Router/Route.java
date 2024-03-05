package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;

import java.nio.ByteBuffer;

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

    public final void sendToClient(NetworkConnectionToClient connectionToClient, byte[] data) {
        connectionToClient.send(getID(), data);
    }

    public final void sendToServer(byte[] data) {
        NetworkClient.send(getID(), data);
    }

    protected final void define(short id) {
        this.id = id;
    }
}
