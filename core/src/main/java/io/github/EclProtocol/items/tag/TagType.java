package io.github.EclProtocol.items.tag;

@SuppressWarnings("unused")
public enum TagType {
    END,
    BYTE,
    INT,
    STRING,
    LIST,
    COMPOUND // 這是核心，相當於 JSON 的 Object {}
}

