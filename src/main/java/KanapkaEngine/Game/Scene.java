package KanapkaEngine.Game;

import KanapkaEngine.Components.World;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public final List<Node> nodes = new ArrayList<>();
    public final World scene_world;

    private int CHUNK_SIZE = 10;

    public Scene() {
        scene_world = new World();
    }

    public Scene(World world) {
        this.scene_world = world;
    }

    public int getChunkSize() {
        return CHUNK_SIZE;
    }

    /**
     * NOT RECOMMENDED TO CHANGE DURING RUN TIME!!!
     * CHUNKS WILL GET MESSED UP AND CHUNKS WILL BE IN THE WRONG
     * PLACES!!!
     *
     * RUN ONLY BEFORE WORLD CREATION!!!
     * @param CHUNK_SIZE
     */
    public void setChunkSize(int CHUNK_SIZE) {
        this.CHUNK_SIZE = CHUNK_SIZE;
    }
}
