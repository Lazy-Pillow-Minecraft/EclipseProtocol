package io.github.EclProtocol.registry;

import io.github.EclProtocol.items.Item;
import io.github.EclProtocol.util.GameID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemRegistry {

    private static final Map<GameID, Item> REGISTRY_MAP = new HashMap<>();
    private static final List<Item> REGISTRY_LIST = new ArrayList<>();

    /**
     * 注册一个物品或其子类。
     *
     * @param <T>  物品的类型，必须是 Item 或其子类
     * @param id   物品的 GameID
     * @param item 物品对象
     * @return 注册的物品对象
     */
    public static <T extends Item> T register(GameID id, T item) {
        if (REGISTRY_MAP.containsKey(id)) {
            throw new IllegalArgumentException("冲突！物品名字 " + id + " 已经被注册过了！");
        }
        if (REGISTRY_MAP.containsValue(item)) {
            throw new IllegalArgumentException("冲突！这个物品对象 " + item + " 已经注册过了！");
        }

        item.setRegistryId(id, REGISTRY_LIST.size());

        REGISTRY_MAP.put(id, item);
        REGISTRY_LIST.add(item);

        return item;
    }

    public static <T extends Item> T registerSelf(String id, T item) {
        return register(GameID.createID(id), item);
    }

    public static Item getItem(GameID id) {
        return REGISTRY_MAP.get(id);
    }

    public static Item getById(int id) {
        if (id < 0 || id >= REGISTRY_LIST.size()) {
            return null;
        }
        return REGISTRY_LIST.get(id);
    }

    public static Map<GameID, Item> getMap() {
        return REGISTRY_MAP;
    }

    private ItemRegistry() {}
}
