package KanapkaEngine.Net;

import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;

public class NetworkClient implements Runnable {
    private Thread thread;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    public boolean isRunning = true;

    private static NetworkClient instance;

    private NetworkClient(Socket socket) {
        this.socket = socket;

        RouteManager.onClientConnect(socket.getInetAddress());

        thread = new Thread(this);
        thread.start();
    }

    public static void Connect(String hostName, int port) {
        System.out.println("[CLIENT] Connecting to server.");

        if (instance != null) {
            instance.isRunning = false;
            if (!instance.socket.isClosed()) {
                try {
                    instance.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            instance = new NetworkClient(SocketFactory.getDefault().createSocket(hostName, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[CLIENT] Connected to server.");
    }

    public static void Connect(String hostName) {
        Connect(hostName, NetworkServer.PORT);
    }

    @Override
    public void run() {

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
            in.close();
            out.close();
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            RouteManager.onClientDisconnect();
        }
    }

    public static void send(short id, byte[] data) {
        if (instance != null)
            instance.Isend(id, data);
    }

    private void Isend(short id, byte[] data) {
        try {
            out.writeShort(id);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            System.out.println("[CLIENT] Problem");
        }
    }

    public static boolean isConnected() {
        return instance != null && instance.isRunning && !instance.socket.isClosed();
    }
}
