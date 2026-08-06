package io.github.EclProtocol.util.math;

@SuppressWarnings("unused")
public enum Direction4 implements IDirection {

    NORTH(0, 0, -1, 0, Axis.Z),
    SOUTH(0, 0, 1, 1, Axis.Z),

    WEST(-1, 0, 0, 2, Axis.X),
    EAST(1, 0, 0, 3, Axis.X);


    public final int xOffset, yOffset, zOffset;
    public final int index; // 4方向内部索引

    public final Axis axis;

    Direction4(int x, int y, int z, int index, Axis axis) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
        this.index = index;
        this.axis = axis;
    }

    @Override public int getXOffset() { return xOffset; }
    @Override public int getYOffset() { return yOffset; }
    @Override public int getZOffset() { return zOffset; }

    public Direction4 getOpposite() {
        return values()[this.index ^ 1];
    }

    @Override
    public Axis getAxis() {
        return this.axis;
    }
}
