package KanapkaEngine.Net;

import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class NetworkConnectionToClient implements Runnable {

    private final int id;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Thread thread;

    public NetworkConnectionToClient(Socket socket, int id) {
        System.out.println("[SERVERCLIENT] Setting up server client connection.");
        this.socket = socket;

        this.id = id;

        thread = new Thread(this);
        thread.start();
        System.out.println("[SERVERCLIENT] Set up server client connection.");
    }

    public final int getId() {
        return id;
    }

    @Override
    public void run() {
        System.out.println("[SERVERCLIENT] Started server client connection thread.");

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("[SERVERCLIENT] Failed connection with client " + socket.getLocalAddress().getHostAddress());
            e.printStackTrace();
        }

        try {
            while (!socket.isClosed()) {
                short ID = in.readShort();
                int length = in.readInt();
                Route route = RouteManager.getRoute(ID);
                if (route == null) {
                    System.out.println("[SERVERCLIENT] ROUTE NOT FOUND " + ID);
                    socket.close();
                    return;
                }
                byte[] data = new byte[length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readByte();
                }
                route.ServerClient_IN(this, data);
            }
            in.close();
            out.close();
            if (!socket.isClosed())
                socket.close();
            System.out.println("Closed");
        } catch (IOException e) {
            System.out.println("[SERVERCLIENT] Problem");
        }
        finally {
            RouteManager.onServerClientDisconnect(this);


        }
    }

    public void send(short id, byte[] data) {
        try {
            out.writeShort(id);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
