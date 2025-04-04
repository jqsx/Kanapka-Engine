package KanapkaEngine.Net.Router;

import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.Time;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkServer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

public final class RTTRoute extends Route implements Plugin {
    private static final Random rand = new Random();
    private static final HashMap<NetworkConnectionToClient, RTT> track = new HashMap<>();

    private static final HashMap<NetworkConnectionToClient, Integer> RTT_Times = new HashMap<>();

    public static long maxTime = 5000;

    private static final long MIN_TIME = 500;
    private static final long MAX_TIME = 30000;

    @Override
    public void ServerClient_IN(NetworkConnectionToClient conn, byte[] data) {
        // Receive trip info

        if (data.length != Integer.BYTES)
            return;

        ByteBuffer buffer = ByteBuffer.wrap(data);

        int code = buffer.getInt();

        RTT rtt = track.get(conn);

        if (rtt == null)
            return;

        if (rtt.code != code)
            return;

        long ms = System.currentTimeMillis() - rtt.entryTime;

        RTT_Times.put(conn, (int) ms);

        track.remove(conn);
    }

    public int getRTT(NetworkConnectionToClient conn) {
        return RTT_Times.get(conn);
    }

    @Override
    public void Client_IN(byte[] data) {
        sendToServer(data);
    }

    public void askRTT() {
        for (NetworkConnectionToClient conn : NetworkServer.clients()) {
            RTT rtt = new RTT();
            track.put(conn, rtt);

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

            buffer.putInt(rtt.code);

            sendToClient(conn, buffer.array());
        }
    }

    public void removeUnreceived() {
        for (NetworkConnectionToClient conn : track.keySet()) {
            RTT rtt = track.get(conn);
            long ms = System.currentTimeMillis() - rtt.entryTime;

            long exterminateTrack = Math.min(Math.max(maxTime, MIN_TIME), MAX_TIME);

            if (ms > exterminateTrack)
                track.remove(conn);
        }
    }

    @Override
    public void Apply(Engine engine) {

    }

    @Override
    public void Update() {

    }

    @Override
    public void Detach() {

    }

    private class RTT {
        private long entryTime;

        private int code;

        private RTT() {
            this.entryTime = System.currentTimeMillis();

            this.code = rand.nextInt();
        }
    }
}
