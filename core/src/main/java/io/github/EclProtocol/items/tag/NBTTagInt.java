package io.github.EclProtocol.items.tag;

// 整數
public class NBTTagInt extends NBTTag {
    private final int data;
    public NBTTagInt(int data) { this.data = data; }
    @Override public TagType getType() { return TagType.INT; }
    @Override public Object getData() { return data; }
    public int getInt() { return data; }
}
