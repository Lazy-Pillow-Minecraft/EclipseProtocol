package io.github.EclProtocol.items.tag;

// 字符串
public class NBTTagString extends NBTTag {
    private final String data;
    public NBTTagString(String data) { this.data = data; }
    @Override public TagType getType() { return TagType.STRING; }
    @Override public Object getData() { return data; }
    public String getString() { return data; }
}
