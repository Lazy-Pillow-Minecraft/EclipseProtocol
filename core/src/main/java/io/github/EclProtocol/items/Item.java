package io.github.EclProtocol.items;

import io.github.EclProtocol.config.Constants;
import io.github.EclProtocol.util.GameID;

@SuppressWarnings("unused")
public class Item {
    private final short maxCount;

    private GameID registryName = null;
    private int intId = -1;

    public Item(int maxCount) {
        this.maxCount = (short) maxCount;
    }

    public Item() {
        this.maxCount = Constants.DEFAULT_ITEM_MAX_COUNT;
    }

    public GameID getRegistryName() {
        return registryName;
    }

    public int getIntId() {
        return intId;
    }

    public void setRegistryId(GameID name, int id) {
        if (this.registryName != null) {
            throw new IllegalStateException("物品 " + this + " 已经注册过了，不能重复注册！");
        }
        this.registryName = name;
        this.intId = id;
    }
}
