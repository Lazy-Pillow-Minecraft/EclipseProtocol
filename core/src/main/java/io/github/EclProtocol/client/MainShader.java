package io.github.EclProtocol.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import io.github.EclProtocol.config.Constants;

public class MainShader extends DefaultShader {

    public MainShader(Renderable renderable) {
        super(renderable, createConfig());
    }

    private static DefaultShader.Config createConfig() {
        DefaultShader.Config config = new Config();

        config.vertexShader = Gdx.files.internal("assets/" + Constants.NAME_SPACE + "/shaders/block.vertex.glsl").readString();
        config.fragmentShader = Gdx.files.internal("assets/" + Constants.NAME_SPACE + "/shaders/block.fragment.glsl").readString();
        return config;
    }
}
