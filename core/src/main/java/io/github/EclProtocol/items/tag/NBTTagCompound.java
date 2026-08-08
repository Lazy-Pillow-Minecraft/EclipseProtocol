package io.github.EclProtocol.items.tag;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class NBTTagCompound extends NBTTag {
    public final Map<String, NBTTag> map = new HashMap<>();

    @Override public TagType getType() { return TagType.COMPOUND; }
    @Override public Object getData() { return map; }

    public void setTag(String key, NBTTag tag) {
        map.put(key, tag);
    }

    @SuppressWarnings("unchecked")
    public <T extends NBTTag> T getTag(String key) {
        return (T) map.get(key);
    }

    public void setInt(String key, int value) {
        setTag(key, new NBTTagInt(value));
    }

    public void setString(String key, String value) {
        setTag(key, new NBTTagString(value));
    }

    public int getInt(String key, int defaultValue) {
        NBTTag tag = map.get(key);
        if (tag instanceof NBTTagInt) return ((NBTTagInt) tag).getInt();
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        NBTTag tag = map.get(key);
        if (tag instanceof NBTTagString) return ((NBTTagString) tag).getString();
        return defaultValue;
    }
}
