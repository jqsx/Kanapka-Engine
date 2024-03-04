package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetDataType;
import KanapkaEngine.Net.NetworkConnectionToClient;

public class HelloWorld extends Route {
    public HelloWorld() {
        super(new NetDataType[] {
            NetDataType.INT
        });
        RouteManager.defineRoute(this);
    }

    @Override
    public void ServerClient_IN(NetworkConnectionToClient connectionToClient, byte[] data) {
        System.out.println("Hello to the server.");
    }

    @Override
    public void Client_IN(byte[] data) {
        System.out.println("Hello to the client");
    }
}
