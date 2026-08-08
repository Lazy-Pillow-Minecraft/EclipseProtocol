package io.github.EclProtocol.items;

import io.github.EclProtocol.items.tag.NBTTagCompound;

@SuppressWarnings("unused")
public class ItemStack {
    private Item item;
    private int count;
    private NBTTagCompound nbt;

    public ItemStack(Item item, int count) {
        this.item = item;
        this.count = count;
        this.nbt = new NBTTagCompound();
    }

    public boolean hasTag() {
        return nbt != null && !nbt.map.isEmpty();
    }

    public NBTTagCompound getTag() {
        if (nbt == null) nbt = new NBTTagCompound();
        return nbt;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCount(int Count) {
        this.count = (short) Count;
    }
}

