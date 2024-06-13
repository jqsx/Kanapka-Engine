package KanapkaEngine.Components;

import java.util.ArrayList;

/**
 * Stores <code><strong>BlockData</strong></code> that can be accessed by the chunks and blocks.
 */
public class BlockManager {
    private final static ArrayList<BlockData> blockData = init();
    private BlockManager() {

    }

    /**
     * Registers a new block into the global list.
     * @param data
     */
    public static void createBlock(BlockData data) {
        blockData.add(data);
    }

    /**
     *
     * @param id
     * @return The block data with the corresponding id. Will throw an error if id is out of bounds.
     */
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
