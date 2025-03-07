package KanapkaEngine.Game;

import KanapkaEngine.Components.ImmutableList;
import KanapkaEngine.Components.TSLinkedList;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static final Logger logger = new Logger("SCENE");
    private static Scene currentlyLoaded;

    static {
        loadScene(new Scene());
    }

    private static final ImmutableList<Node> empty = new ImmutableList<>(new ArrayList<>());

    private SceneManager() {

    }

    public static Scene getCurrentlyLoaded() {
        return currentlyLoaded;
    }

    public static ImmutableList<Node> getSceneNodes() {
        if (currentlyLoaded != null)
            return currentlyLoaded.getNodes();
        return empty;
    }

    public static Scene loadScene(Scene scene) {
        return currentlyLoaded = scene;
    }

    public static void addNode(Node node) {
        if (currentlyLoaded == null) return;

        currentlyLoaded.appendNode(node);
    }

    public static void removeNode(Node node) {
        if (currentlyLoaded != null) {
            currentlyLoaded.removeNode(node);
        }
    }

    public static boolean hasScene() {
        return currentlyLoaded != null;
    }
}
