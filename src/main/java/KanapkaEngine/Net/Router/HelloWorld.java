package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkConnectionToClient;

public class HelloWorld extends Route {
    public HelloWorld() {
        super();
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
