package KanapkaEngine.Net;

import KanapkaEngine.Components.Component;
import KanapkaEngine.Game.WIP;

@WIP
public class NetworkComponent extends Component {
    public NetworkComponent() {

    }

    public final void SendTrackedData() {
        NetworkIdentity identity = getParent().getComponent(NetworkIdentity.class);

        if (identity == null) {
            throw new NullPointerException("Missing NetworkIdentity");
        }


    }
}
