package KanapkaEngine.Net;

import KanapkaEngine.Components.Component;

public class NetworkIdentity extends Component {
    private final int NetID;

    public NetworkIdentity(int NetID) {
        this.NetID = NetID;
    }

    public int getNetID() {
        return NetID;
    }
}
