package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkConnectionToClient;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {
    private static final List<Route> routes = new ArrayList<>();

    public static Route getRoute(short id) {
        return routes.get(id);
    }

    public static final HelloWorld helloRoute = getHelloRoute();

    private static HelloWorld getHelloRoute() {
        HelloWorld helloWorld = new HelloWorld();
        routes.add(helloWorld);
        return helloWorld;
    }

    private RouteManager() {

    }

    public static void defineRoute(Route route) {
        if (route.isReady) return;
        for (Route other : routes) {
            if (route.getClass() == other.getClass())
                return;
        }
        route.isReady = true;
        route.define((short) routes.size());
        routes.add(route);
    }

    public static void onServerStart() {
        routes.forEach(Route::onServerStart);
    }

    public static void onServerStop() {
        routes.forEach(Route::onServerStop);
    }

    public static void onClientConnect(InetAddress address) {
        routes.forEach(route -> route.onClientConnect(address));
    }

    public static void onClientDisconnect() {
        routes.forEach(Route::onClientDisconnect);
    }

    public static void onServerClientDisconnect(NetworkConnectionToClient conn) {
        routes.forEach(route -> route.onServerClientDisconnect(conn));
    }

    public static void onServerClientConnect(NetworkConnectionToClient conn) {
        routes.forEach(route -> route.onServerClientConnect(conn));
    }
}
