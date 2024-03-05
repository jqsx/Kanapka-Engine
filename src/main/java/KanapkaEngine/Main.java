package KanapkaEngine;

import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Editor.Editor;
import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkServer;
import KanapkaEngine.Net.Router.HelloWorld;
import KanapkaEngine.Net.Router.RouteManager;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws Exception {
//        Editor.StartEditor();

        HelloWorld helloWorld = new HelloWorld();

        RouteManager.defineRoute(helloWorld);

        NetworkServer.StartServer();

        NetworkClient.Connect("localhost");

        Thread.sleep(1000L);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(555);

        System.out.println("Sending data");
        helloWorld.sendToServer(buffer.array());
        System.out.println("Sent data.");

        for (NetworkConnectionToClient conn : NetworkServer.clients) {
            helloWorld.sendToClient(conn, buffer.array());
        }
    }
}