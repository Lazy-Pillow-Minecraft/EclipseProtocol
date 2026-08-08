package io.github.EclProtocol.init;

import io.github.EclProtocol.items.Item;
import io.github.EclProtocol.registry.ItemRegistry;

public class Items {

    public static final Item TEST = ItemRegistry.registerSelf("test", new Item(2));

    private Items() {}
}
