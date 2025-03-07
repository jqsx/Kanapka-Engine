package KanapkaEngine.Net;

import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.Logger;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.Time;
import KanapkaEngine.Net.Router.RouteManager;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class NetworkServer implements Runnable {
    private static final Logger logger = new Logger("NETWORK_SERVER");
    public static int PORT = 6969;

    public static String hostName = "localhost";

    public boolean isRunning = true;

    private static ServerSocket serverSocket;

    public static final List<NetworkConnectionToClient> clients = new ArrayList<>();

    private static Thread serverThread;

    private static NetworkServer instance;

    private static HashMap<Integer, NetworkConnectionToClient> connections = new HashMap<>();

    public static boolean isServer = false;

    public static int ServerTickRate = 30;

    private static double lastTick = Time.time();

    public static void StartServer() {
        try {
            if (serverSocket != null)
                RouteManager.onServerStop();
            serverSocket = ServerSocketFactory.getDefault().createServerSocket();
            logger.log("Created server socket.");
            serverSocket.bind(new InetSocketAddress(hostName != null && !hostName.isEmpty() ? hostName : getLANIP(), PORT));
            logger.log("Bound server to correct hostName and port.");
            clients.clear();
            logger.log("Cleared existing clients.");
            if (serverThread != null)
                instance.isRunning = false;

            logger.log("Starting server thread.");
            serverThread = new Thread(instance = new NetworkServer());
            serverThread.start();

            isServer = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLANIP() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("You are most likely not connected to the internet and the server cannot be launched.");
            return "localhost";
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ServerThread");
        logger.log("Started server thread.");
        RouteManager.onServerStart();
        while (isRunning && !serverSocket.isClosed()) {
            try {
                logger.log("Awaiting connection.");
                Socket socket = serverSocket.accept();
                logger.log("Connection at " + socket.getInetAddress().getHostAddress());
                NetworkConnectionToClient conn = new NetworkConnectionToClient(socket, getFreeID());

                connections.put(conn.getId(), conn);

                clients.add(conn);
                RouteManager.onServerClientConnect(conn);
            } catch (IOException e) {
                if (serverSocket.isClosed())
                    return;

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

        isServer = false;
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

    /**
     * Put this method in the global update method of the engine, you can control how quick does the server tick with <code>NetworkServer.ServerTickRate</code>
     */
    public static void ServerTick() {
        if (lastTick + 1.0 / (double)ServerTickRate < Time.time()) {
            lastTick = Time.time();

            NetworkOperation.ExecuteNetworkOperations();
        }
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

    protected static void IClosed(NetworkConnectionToClient conn) {
        if (conn.isClosed())
            connections.remove(conn.getId());
    }
}
