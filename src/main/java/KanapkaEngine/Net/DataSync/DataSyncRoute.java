package KanapkaEngine.Net.DataSync;

import KanapkaEngine.Game.WIP;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.Router.Route;

@WIP
public class DataSyncRoute extends Route {
    protected static DataSyncRoute dataSyncRoute;

    public static void Init() {
        if (dataSyncRoute == null)
            dataSyncRoute = new DataSyncRoute();
    }

    @Override
    public void ServerClient_IN(NetworkConnectionToClient conn, byte[] data) {

    }

    @Override
    public void Client_IN(byte[] data) {

    }
}
