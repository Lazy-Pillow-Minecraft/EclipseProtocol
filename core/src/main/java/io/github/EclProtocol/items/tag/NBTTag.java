package io.github.EclProtocol.items.tag;

import java.util.List;

@SuppressWarnings("unused")
public abstract class NBTTag {
    public abstract TagType getType();
    public abstract Object getData();

    public static NBTTagCompound createCompound() { return new NBTTagCompound(); }
    public static NBTTagInt createInt(int value) { return new NBTTagInt(value); }
    public static NBTTagString createString(String value) { return new NBTTagString(value); }
    public static NBTTagList createList(List<NBTTag> value) { return new NBTTagList(value); }
}

