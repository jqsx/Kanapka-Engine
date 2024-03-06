package KanapkaEngine.Net;

import KanapkaEngine.Net.Router.RouteManager;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkServer implements Runnable {
    public static int PORT = 6969;

    public static String hostName = "localhost";

    public boolean isRunning = true;

    private static ServerSocket serverSocket;

    public static final List<NetworkConnectionToClient> clients = new ArrayList<>();

    private static Thread serverThread;

    private static NetworkServer instance;

    public static void StartServer() {
        try {
            if (serverSocket != null)
                RouteManager.onServerStop();
            serverSocket = ServerSocketFactory.getDefault().createServerSocket();
            System.out.println("[SERVER] Created server socket.");
            serverSocket.bind(new InetSocketAddress(hostName != null && !hostName.isEmpty() ? hostName : "localhost", PORT));
            System.out.println("[SERVER] Bound server to correct hostName and port.");
            clients.clear();
            System.out.println("[SERVER] Cleared existing clients.");
            if (serverThread != null)
                instance.isRunning = false;

            System.out.println("[SERVER] Starting server thread.");
            serverThread = new Thread(instance = new NetworkServer());
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("[SERVER] Started server thread.");
        RouteManager.onServerStart();
        while (isRunning && !serverSocket.isClosed()) {
            try {
                System.out.println("[SERVER] Awaiting connection.");
                Socket socket = serverSocket.accept();
                System.out.println("[SERVER] Connection at " + socket.getInetAddress().getHostAddress());
                NetworkConnectionToClient conn = new NetworkConnectionToClient(socket);
                clients.add(conn);
                RouteManager.onServerClientConnect(conn);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        RouteManager.onServerStop();
        serverSocket = null;
        serverThread = null;
        instance = null;
    }

    private NetworkServer() {

    }
}
