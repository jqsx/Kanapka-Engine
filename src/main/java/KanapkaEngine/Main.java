package KanapkaEngine;

import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Components.Rigidbody;
import KanapkaEngine.Editor.Editor;
import KanapkaEngine.Net.NetworkClient;
import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkServer;
import KanapkaEngine.Net.Router.HelloWorld;
import KanapkaEngine.Net.Router.Route;
import KanapkaEngine.Net.Router.RouteManager;
import KanapkaEngine.UI.Image;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws Exception {
        Editor.StartEditor();
    }

    public static class Test {
        public int a = 0;

        public Test() {

        }
    }
}