package io.github.EclProtocol.debug;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.EclProtocol.registry.BlockRegistry;
import io.github.EclProtocol.registry.ItemRegistry;

public class RegistryViewer extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // 深灰色背景
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float y = Gdx.graphics.getHeight() - 20;

        font.draw(batch, "=== Block Registry (" + BlockRegistry.getMap().size() + ") ===", 10, y);
        y -= 25;
        for (java.util.Map.Entry<io.github.EclProtocol.util.GameID, io.github.EclProtocol.blocks.Block> entry : BlockRegistry.getMap().entrySet()) {
            font.draw(batch, entry.getKey().getNameSpace() + ": " + entry.getKey().getId(), 10, y);
            y -= 20;
        }

        y -= 20;
        font.draw(batch, "=== Item Registry (" + ItemRegistry.getMap().size() + ") ===", 10, y);
        y -= 25;
        for (java.util.Map.Entry<io.github.EclProtocol.util.GameID, io.github.EclProtocol.items.Item> entry : ItemRegistry.getMap().entrySet()) {
            font.draw(batch, entry.getKey().getNameSpace() + ": " + entry.getKey().getId(), 10, y);
            y -= 20;
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
