package io.github.EclProtocol.util.math;

@SuppressWarnings("unused")
public class Int3Pos {
    public final int x;
    public final int y;
    public final int z;
    public Int3Pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Int3Pos add(int dx, int dy, int dz) {
        return new Int3Pos(this.x + dx, this.y + dy, this.z + dz);
    }

    public Int3Pos sub(int dx, int dy, int dz) {
        return new Int3Pos(this.x - dx, this.y - dy, this.z - dz);
    }

    public Int3Pos mul(int scale) {
        return new Int3Pos(this.x * scale, this.y * scale, this.z * scale);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Int3Pos int3Pos = (Int3Pos) o;
        return x == int3Pos.x && y == int3Pos.y && z == int3Pos.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "IntPos(" + x + ", " + y + ", " + z + ")";
    }
}
