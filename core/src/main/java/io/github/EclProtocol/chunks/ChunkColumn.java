package io.github.EclProtocol.chunks;

import io.github.EclProtocol.util.math.Int2Pos;
import io.github.EclProtocol.util.math.Int3Pos;

@SuppressWarnings("unused")
public class ChunkColumn {
    private final int columnHeight;
    private final Chunk[] chunks;
    private final Int2Pos columnPos;
    public ChunkColumn(int columnHeight, Int2Pos columnPos) {
        this.columnHeight = columnHeight;
        this.columnPos = columnPos;
        this.chunks = new Chunk[columnHeight];
        for (int i = 0; i < columnHeight; i++) {
            chunks[i] = new Chunk(new Int3Pos(columnPos.x, i, columnPos.y));
        }
    }

    public int getHeight() {
        return columnHeight;
    }

    public void setChunk(int yLevel, Chunk chunk) {
        if (yLevel >= 0 && yLevel < columnHeight) {
            chunks[yLevel] = chunk;
        }
    }

    public Chunk getChunk(int yLevel) {
        if (yLevel >= 0 && yLevel < columnHeight) {
            return chunks[yLevel];
        }
        return null;
    }

    public Int2Pos getColumnPos() {
        return columnPos;
    }
}
