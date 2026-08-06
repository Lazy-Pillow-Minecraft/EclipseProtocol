package io.github.EclProtocol.client.model;

public class CubeModelDefinition {
    public Object up;
    public Object down;
    public Object north;
    public Object south;
    public Object west;
    public Object east;

    public CubeModelDefinition(Object up, Object down, Object north, Object south, Object west, Object east) {
        this.up = up;
        this.down = down;
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
    }
}
