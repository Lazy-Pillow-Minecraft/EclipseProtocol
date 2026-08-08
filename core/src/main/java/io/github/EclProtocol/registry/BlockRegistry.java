package io.github.EclProtocol.registry;

import io.github.EclProtocol.blocks.Block;
import io.github.EclProtocol.items.Item;
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

    public static <T extends Block> T registerSelf(String id, T block) {
        return register(GameID.createID(id), block);
    }

    /**
     * 一次性註冊方塊和對應的物品，它們共用同一個 GameID。
     *
     * @param <B> 方塊類型
     * @param <I> 物品類型
     * @param id 註冊用的 ID
     * @param block 方塊實例
     * @param item 物品實例
     * @return 返回方塊實例
     */
    public static <B extends Block, I extends Item> B registerWithItem(GameID id, B block, I item) {
        register(id, block);
        ItemRegistry.register(id, item);

        return block;
    }

    public static <B extends Block, I extends Item> B registerWithItemSelf(String id, B block, I item) {
        return registerWithItem(GameID.createID(id), block, item);
    }

    public static Block getBlock(GameID id) {
        return REGISTRY_MAP.get(id);
    }

    public static Block getById(int id) {
        if (id < 0 || id >= REGISTRY_LIST.size()) return null;
        return REGISTRY_LIST.get(id);
    }

    public static Map<GameID, Block> getMap() {
        return REGISTRY_MAP;
    }

    private BlockRegistry() {}
}
