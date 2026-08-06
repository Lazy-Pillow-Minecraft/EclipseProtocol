package io.github.EclProtocol.util.math;

public class Mth {
    public static int loopCoord(int value, int range) {
        return (value % range + range) % range;
    }
    public static float loopCoord(float value, float range) {
        return (value % range + range) % range;
    }
    public static double loopCoord(double value, double range) {
        return (value % range + range) % range;
    }
}
