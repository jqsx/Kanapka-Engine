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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NetworkServer implements Runnable {
    public static int PORT = 6969;

    public static String hostName = "localhost";

    public boolean isRunning = true;

    private static ServerSocket serverSocket;

    public static final List<NetworkConnectionToClient> clients = new ArrayList<>();

    private static Thread serverThread;

    private static NetworkServer instance;

    private static HashMap<Integer, NetworkConnectionToClient> connections = new HashMap<>();

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
                NetworkConnectionToClient conn = new NetworkConnectionToClient(socket, getFreeID());

                connections.put(conn.getId(), conn);

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

    public static NetworkConnectionToClient getConn(int id) {
        if (connections.containsKey(id)) {
            NetworkConnectionToClient conn = connections.get(id);

            if (!conn.isClosed())
                return conn;
            else connections.remove(id);
        }
        return null;
    }

    private NetworkServer() {

    }

    private int getFreeID() {
        Random random = new Random();
        int r;

        while (connections.containsKey(r = Math.abs(random.nextInt()))) {

        }
        return r;
    }
}
