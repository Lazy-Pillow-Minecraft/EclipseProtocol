package io.github.EclProtocol.blocks.property;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PropertyInteger implements IProperty<Integer> {
    private final String name;
    private final int min;
    private final int max;

    public PropertyInteger(String name, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    @Override public String getName() { return name; }

    @Override
    public boolean isValid(Integer value) {
        return value >= min && value <= max;
    }

    @Override
    public List<Integer> getAllowedValues() {
        List<Integer> r = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            r.add(i);
        }
        return r;
    }
}
