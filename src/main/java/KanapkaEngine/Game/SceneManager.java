package KanapkaEngine.Game;

import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.TSLinkedList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SceneManager {
    private static Scene currentlyLoaded;

    private SceneManager() {

    }

    public static Scene getCurrentlyLoaded() {
        return currentlyLoaded;
    }

    public static TSLinkedList<Node> getSceneNodes() {
        if (currentlyLoaded != null)
            return currentlyLoaded.nodes;
        return new TSLinkedList<>();
    }

    public static Scene loadScene(Scene scene) {
        if (scene.loaded())
            return currentlyLoaded = scene;
        System.out.println("Scene isn't loaded");
        return null;
    }

    public static void addNode(Node node) {
        if (currentlyLoaded == null) return;
        if (node.getParent() != null)
            node.setParent(null);
        else
            currentlyLoaded.nodes.addEnd(node);
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
