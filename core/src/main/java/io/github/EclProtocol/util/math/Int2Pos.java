package io.github.EclProtocol.util.math;

@SuppressWarnings("unused")
public class Int2Pos {
    public final int x;
    public final int y;
    public Int2Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Int2Pos add(int dx, int dy) {
        return new Int2Pos(this.x + dx, this.y + dy);
    }

    public Int2Pos sub(int dx, int dy) {
        return new Int2Pos(this.x - dx, this.y - dy);
    }

    public Int2Pos mul(int scale) {
        return new Int2Pos(this.x * scale, this.y * scale);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Int2Pos int3Pos = (Int2Pos) o;
        return x == int3Pos.x && y == int3Pos.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "IntPos(" + x + ", " + y + ")";
    }
}
