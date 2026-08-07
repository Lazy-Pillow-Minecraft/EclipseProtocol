package io.github.EclProtocol.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import io.github.EclProtocol.client.model.CubeModelDefinition;
import io.github.EclProtocol.util.GameID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubeModelLoader {
    private static final Map<GameID, CubeModelDefinition> cache = new HashMap<>();
    private static final String JSON_BASE_PATH_1 = "assets/";
    private static final String JSON_BASE_PATH_2 = "/cubes/";

    public static CubeModelDefinition getModel(GameID blockId) {
        if (cache.containsKey(blockId)) {
            return cache.get(blockId);
        }

        String jsonPath = JSON_BASE_PATH_1 + blockId.getNameSpace() + JSON_BASE_PATH_2 + blockId.getId() + ".json";
        FileHandle file = Gdx.files.internal(jsonPath);

        if (!file.exists()) {
            Gdx.app.error("CubeModelLoader", "找不到模型文件: " + jsonPath);
            return null;
        }

        CubeModelDefinition definition = parseJson(file);
        if (definition != null) {
            cache.put(blockId, definition);
        }
        return definition;
    }

    private static CubeModelDefinition parseJson(FileHandle file) {
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(file);
        JsonValue textures = root.get("textures");
        if (textures == null) return null;

        Object up = resolveTexturePath(textures.get("up"));
        Object down = resolveTexturePath(textures.get("down"));
        Object north = resolveTexturePath(textures.get("north"));
        Object south = resolveTexturePath(textures.get("south"));
        Object west = resolveTexturePath(textures.get("west"));
        Object east = resolveTexturePath(textures.get("east"));

        return new CubeModelDefinition(up, down, north, south, west, east);
    }

    private static Object resolveTexturePath(JsonValue jsonValue) {
        if (jsonValue == null) return "block/stone";
        if (jsonValue.isString()) return jsonValue.asString();

        if (jsonValue.isArray()) {
            if (jsonValue.size == 0) return "block/stone";

            List<JsonValue> variants = new ArrayList<>();
            for (int i = 0; i < jsonValue.size; i++) {
                variants.add(jsonValue.get(i));
            }
            return variants;
        }
        return "block/stone";
    }
}
