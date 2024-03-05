package KanapkaEngine.Net;

import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;

public class NetworkClient implements Runnable {
    private static Thread thread;
    private static Socket socket;

    private static DataInputStream in;
    private static DataOutputStream out;

    public boolean isRunning = true;

    private static NetworkClient instance;

    private NetworkClient() {

    }

    public static void Connect(String hostName, int port) {
        System.out.println("[CLIENT] Connecting to server.");
        try {
            if (instance != null) {
                instance.isRunning = false;
            }
            socket = SocketFactory.getDefault().createSocket(hostName, port);

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            thread = new Thread(instance = new NetworkClient());
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[CLIENT] Connected to server.");
    }

    public static void Connect(String hostName) {
        Connect(hostName, NetworkServer.PORT);
    }

    @Override
    public void run() {
        try {
            while (isRunning && !socket.isClosed()) {
                short ID = in.readShort();
                int length = in.readInt();
                Route route = RouteManager.getRoute(ID);
                if (route == null) {
                    System.out.println("[CLIENT] ROUTE NOT FOUND " + ID);
                    socket.close();
                    return;
                }
                byte[] data = new byte[length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readByte();
                }
                route.Client_IN(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(short id, byte[] data) {
        try {
            out.writeShort(id);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            System.out.println("[CLIENT] Problem");
        }
    }
}
