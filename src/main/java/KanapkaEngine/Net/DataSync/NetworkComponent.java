package KanapkaEngine.Net.DataSync;

import KanapkaEngine.Game.Component;
import KanapkaEngine.Game.WIP;
import KanapkaEngine.Net.NetworkIdentity;

@WIP
public class NetworkComponent extends Component {

    public NetworkComponent() {

    }

    private void LocateTrackedData() {

    }

    protected final void s_Send() {
        NetworkIdentity identity = getParent().getComponent(NetworkIdentity.class);

        if (identity == null) {
            throw new NullPointerException("Missing NetworkIdentity");
        }


    }
}
