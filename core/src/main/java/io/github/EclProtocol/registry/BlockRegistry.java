package io.github.EclProtocol.registry;

import io.github.EclProtocol.blocks.Block;
import io.github.EclProtocol.util.GameID;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BlockRegistry {
    private static final Map<GameID, Block> REGISTRY_MAP = new HashMap<>();
    private static final java.util.List<Block> REGISTRY_LIST = new java.util.ArrayList<>();

    /**
     * 注册一个方块或其子类。
     * @param <T> 方块的类型，必须是 Block 或其子类
     * @param id 方块的 GameID
     * @param block 方块对象
     * @return 注册的方块对象
     */
    public static <T extends Block> T register(GameID id, T block) {
        if (REGISTRY_MAP.containsKey(id)) {
            throw new IllegalArgumentException("冲突！方块名字 " + id + " 已经被注册过了！");
        }
        if (REGISTRY_MAP.containsValue(block)) {
            throw new IllegalArgumentException("冲突！这个方块对象 " + block + " 已经注册过了！");
        }
        block.setRegistryId(id, REGISTRY_LIST.size());
        REGISTRY_MAP.put(id, block);
        REGISTRY_LIST.add(block);
        return block;
    }

    public static Block getBlock(GameID id) {
        return REGISTRY_MAP.get(id);
    }

    public static Block getById(int id) {
        if (id < 0 || id >= REGISTRY_LIST.size()) return null;
        return REGISTRY_LIST.get(id);
    }

    private BlockRegistry() {}
}
