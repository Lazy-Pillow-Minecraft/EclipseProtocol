package io.github.EclProtocol.util.math;

@SuppressWarnings("unused")
public enum Direction6 {
    NORTH(0, 0, -1, 0, Axis.Z),
    SOUTH(0, 0, 1, 1, Axis.Z),

    WEST(-1, 0, 0, 2, Axis.X),
    EAST(1, 0, 0, 3, Axis.X),

    DOWN(0, -1, 0, 4, Axis.Y),
    UP(0, 1, 0, 5, Axis.Y);

    public final int xOffset;
    public final int yOffset;
    public final int zOffset;

    public final int index;

    public final Axis axis;

    private final Direction4 horizontal;

    Direction6(int x, int y, int z, int index, Axis axis) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
        this.index = index;
        this.axis = axis;

        this.horizontal = findHorizontal();
    }

    private Direction4 findHorizontal() {
        if (this.axis == Axis.Y) return null;
        for (Direction4 d : Direction4.values()) {
            if (d.xOffset == this.xOffset && d.zOffset == this.zOffset) {
                return d;
            }
        }
        return null;
    }

    public Direction4 getHorizontal() {
        return horizontal;
    }

    private static final Direction6[] BY_INDEX = new Direction6[6];
    static {
        for (Direction6 dir : values()) {
            BY_INDEX[dir.index] = dir;
        }
    }

    public static Direction6 byIndex(int index) {
        if (index < 0 || index >= 6) return null;
        return BY_INDEX[index];
    }

    public Direction6 getOpposite() {
        return byIndex(this.index ^ 1);
    }

    public Int3Pos offset(Int3Pos pos) {
        return new Int3Pos(pos.x + xOffset, pos.y + yOffset, pos.z + zOffset);
    }
}
