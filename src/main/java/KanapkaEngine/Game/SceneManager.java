package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static final Logger logger = new Logger("SCENE");
    private static Scene currentlyLoaded;

    private static final List<Node> empty = new ArrayList<>();

    private SceneManager() {

    }

    public static Scene getCurrentlyLoaded() {
        return currentlyLoaded;
    }

    public static List<Node> getSceneNodes() {
        if (currentlyLoaded != null)
            return currentlyLoaded.nodes;
        empty.clear();
        return empty;
    }

    public static Scene loadScene(Scene scene) {
        if (scene.loaded())
            return currentlyLoaded = scene;
        logger.warn("Scene isn't loaded");
        return null;
    }

    public static void addNode(Node node) {
        if (currentlyLoaded == null) return;
        if (node.getParent() != null)
            node.setParent(null);
        else
            currentlyLoaded.nodes.add(node);
    }

    public static double getGlobalSize() {
        if (currentlyLoaded != null)
            return currentlyLoaded.getGlobalSize();
        return 1;
    }

    public static void removeNode(Node node) {
        if (currentlyLoaded != null) {
            currentlyLoaded.nodes.remove(node);
        }
    }

    public static boolean hasScene() {
        return currentlyLoaded != null;
    }
}
