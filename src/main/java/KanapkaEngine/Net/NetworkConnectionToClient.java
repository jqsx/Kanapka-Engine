package KanapkaEngine.Net;

import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class NetworkConnectionToClient implements Runnable {
    private static HashMap<Short, Route> routes = new HashMap<>();
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;
    private final Thread thread;

    public NetworkConnectionToClient(Socket socket) {
        System.out.println("Setting up server client connection.");
        this.socket = socket;
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Failed connection with client " + socket.getLocalAddress().getHostAddress());
            e.printStackTrace();
        }

        thread = new Thread(this);
        thread.start();
        System.out.println("Set up server client connection.");
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                System.out.println("Awaiting Data");
                short ID = in.readShort();
                System.out.println("Received Data");
                Route route = RouteManager.getRoute(ID);
                if (route == null) {
                    System.out.println("ROUTE NOT FOUND " + ID);
                    socket.close();
                    return;
                }
                byte[] data = new byte[route.getSize()];
                boolean readEnd = false;
                int i = 0;
                while (!readEnd) {
                    data[i] = in.readByte();
                    i++;
                    if (i >= route.getSize())
                        readEnd=true;
                }
                route.ServerClient_IN(this, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(short id, byte[] data) {
        try {
            out.writeShort(id);
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
