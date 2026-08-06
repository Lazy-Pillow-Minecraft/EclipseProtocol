package io.github.EclProtocol.registry;

import io.github.EclProtocol.blocks.Block;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BlockRegistry {
    private static final Map<String, Block> REGISTRY_MAP = new HashMap<>();
    private static final java.util.List<Block> REGISTRY_LIST = new java.util.ArrayList<>();

    public static Block register(String id, Block block) {
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

    public static Block getBlock(String id) {
        return REGISTRY_MAP.get(id);
    }

    public static Block getById(int id) {
        if (id < 0 || id >= REGISTRY_LIST.size()) return null;
        return REGISTRY_LIST.get(id);
    }

    private BlockRegistry() {}
}
