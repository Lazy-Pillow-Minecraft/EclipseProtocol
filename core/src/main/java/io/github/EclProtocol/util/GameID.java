package io.github.EclProtocol.util;

import io.github.EclProtocol.config.Constants;

import java.util.Objects;

public class GameID {
    private final String nameSpace;
    private final String id;
    public GameID(String nameSpace, String id) {
        this.nameSpace = nameSpace;
        this.id = id;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameID gameID = (GameID) o;
        return Objects.equals(nameSpace, gameID.nameSpace) &&
            Objects.equals(id, gameID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameSpace, id);
    }

    public static GameID createID(String name) {
        return new GameID(Constants.NAME_SPACE, name);
    }
}
