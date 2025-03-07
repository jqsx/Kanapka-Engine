package KanapkaEngine.Net;

import KanapkaEngine.Game.Logger;
import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

public final class NetworkClient implements Runnable {
    private static final Logger logger = new Logger("NETWORK_CLIENT");
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
        logger.log("Connecting to server.");

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

        if (hostName == null || hostName.isEmpty() || hostName.equals("localhost")) {
            hostName = getLANIP();
        }

        try {
            instance = new NetworkClient(SocketFactory.getDefault().createSocket(hostName, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.log("Connected to server.");
    }

    private static String getLANIP() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("You are most likely not connected to the internet and the server cannot be launched.");
            return "localhost";
        }
    }

    public static void Connect(String hostName) {
        Connect(hostName, NetworkServer.PORT);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ClientThread");
        logger.log("Started client thread.");

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
                    logger.error("ROUTE NOT FOUND " + ID);

                    in.skipBytes(in.available());
                    continue;
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
        if (!isConnected())
            return;

        if (instance != null)
            instance.Isend(id, data);
    }

    private void Isend(short id, byte[] data) {
        try {
            out.writeShort(id);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            logger.error(e, "Error while sending message.");
        }
    }

    public static boolean isConnected() {
        return instance != null && instance.isRunning && !instance.socket.isClosed();
    }
}
