package KanapkaEngine.Net.DataSync;

import KanapkaEngine.Components.Component;
import KanapkaEngine.Game.WIP;
import KanapkaEngine.Net.NetworkIdentity;

import java.util.Set;

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
