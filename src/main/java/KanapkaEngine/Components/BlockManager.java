package KanapkaEngine.Components;

import java.util.ArrayList;

public class BlockManager {
    private final static ArrayList<BlockData> blockData = init();
    private BlockManager() {

    }

    public static void createBlock(BlockData data) {
        blockData.add(data);
    }

    public static BlockData getBlockData(int id) {
        return blockData.get(id);
    }

    private static ArrayList<BlockData> init() {
        ArrayList<BlockData> data = new ArrayList<>();
        data.add(new BlockData("wooden.png"));
        return data;
    }

    public static int getBlockCount() {
        return blockData.size();
    }
}
