package KanapkaEngine.Net;

import KanapkaEngine.Components.NodeComponent;

public class NetworkIdentity extends NodeComponent {
    private final int NetID;

    public NetworkIdentity(int NetID) {
        this.NetID = NetID;
    }

    public int getNetID() {
        return NetID;
    }
}
