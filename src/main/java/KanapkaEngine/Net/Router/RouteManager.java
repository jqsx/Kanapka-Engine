package KanapkaEngine.Net.Router;

import java.util.ArrayList;
import java.util.List;

public class RouteManager {
    private static final List<Route> routes = new ArrayList<>();

    public static Route getRoute(short id) {
        return routes.get(id);
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
}
