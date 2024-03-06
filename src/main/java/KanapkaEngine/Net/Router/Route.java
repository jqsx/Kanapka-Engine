package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkServer;

import java.net.InetAddress;

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
}
