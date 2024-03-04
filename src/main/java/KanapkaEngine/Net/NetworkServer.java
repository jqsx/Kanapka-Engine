package KanapkaEngine.Net;

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

    private static final List<NetworkConnectionToClient> clients = new ArrayList<>();

    private static Thread serverThread;

    private static NetworkServer instance;

    public static void StartServer() {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket();
            serverSocket.bind(new InetSocketAddress(hostName != null && !hostName.isEmpty() ? hostName : "localhost", PORT));

            clients.clear();
            if (serverThread != null)
                instance.isRunning = false;

            serverThread = new Thread(instance = new NetworkServer());
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();

                clients.add(new NetworkConnectionToClient(socket));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private NetworkServer() {

    }
}
