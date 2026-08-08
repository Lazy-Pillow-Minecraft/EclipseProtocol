package io.github.EclProtocol.items.tag;

import java.util.ArrayList;
import java.util.List;

// 列表
@SuppressWarnings("unused")
public class NBTTagList extends NBTTag {
    private final List<NBTTag> list = new ArrayList<>();

    public NBTTagList(List<NBTTag> value) {
        super();
    }

    @Override public TagType getType() { return TagType.LIST; }
    @Override public Object getData() { return list; }

    public void addTag(NBTTag tag) { list.add(tag); }
    public NBTTag get(int i) { return list.get(i); }
    public int size() { return list.size(); }
}
