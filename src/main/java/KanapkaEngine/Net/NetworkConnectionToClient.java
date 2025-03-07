package KanapkaEngine.Net;

import KanapkaEngine.Game.Logger;
import KanapkaEngine.Net.Router.RTTRoute;
import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public final class NetworkConnectionToClient implements Runnable {

    private static final Logger logger = new Logger("NETWORK_SERVER_CLIENT");

    private final int id;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Thread thread;

    private int rtt = 0;

    public NetworkConnectionToClient(Socket socket, int id) {
        logger.log("Setting up server client connection.");
        this.socket = socket;

        this.id = id;

        thread = new Thread(this);
        thread.start();
        logger.log("Set up server client connection.");
    }

    public final void getRTTForClient(RTTRoute route) {
        rtt = route.getRTT(this);
    }

    public final int rtt() {
        return rtt;
    }

    public final int getId() {
        return id;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("NetConn=" + id);
        logger.log("Started server client connection thread.");

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error("Failed connection with client " + socket.getLocalAddress().getHostAddress());
            e.printStackTrace();
        }

        try {
            while (!socket.isClosed()) {
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
                route.ServerClient_IN(this, data);
            }
        } catch (IOException e) {
            logger.error("Problem");
        }
        finally {
            try {
                in.close();
                out.close();
                if (!socket.isClosed())
                    socket.close();
                logger.log("Closed");
            } catch (IOException e) {

            }
            RouteManager.onServerClientDisconnect(this);
        }
    }

    /**
     * Buffers the route id, the length of the data and the data into a network message
     * @param id
     * @param data
     */
    public void send(short id, byte[] data) {
        if (isClosed()) {
            NetworkServer.IClosed(this);
            return;
        }

        try {
            out.writeShort(id);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            logger.error(e, "Error while sending message.");
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
