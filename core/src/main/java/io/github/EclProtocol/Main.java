package io.github.EclProtocol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.EclProtocol.chunks.Chunk;
import io.github.EclProtocol.chunks.ChunkColumn;
import io.github.EclProtocol.client.ChunkMeshBuilder;
import io.github.EclProtocol.util.math.Int2Pos;
import io.github.EclProtocol.worldgen.World;
import io.github.EclProtocol.worldgen.generator.SimplexTerrainGenerator;
import io.github.EclProtocol.worldgen.generator.WorldGenerator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class Main extends ApplicationAdapter implements InputProcessor {
    private final int CHUNKS_PER_FRAME = 1000;
    private int loadingCx = 0;
    private int loadingCz = 0;
    private int loadingCy = 0;

    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private boolean isLoading = true;
    private int loadStep = 0;

    private Environment environment;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ChunkMeshBuilder meshBuilder;
    private World world;
    private List<ModelInstance> chunkInstances;

    private boolean isMouseLocked = false;
    private final float walkSpeed = 30f; // 移动速度
    private final float mouseSensitivity = 0.1f; // 鼠标灵敏度

    private float yaw = 0f;   // 水平旋转角
    private float pitch = 0f; // 俯仰角

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public void create() {
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        if (camera == null) return;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void render() {
        if (isLoading) {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            switch (loadStep) {
                case 0:
                    environment = new Environment();
                    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
                    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -2.5f, 1.3f));
                    modelBatch = new ModelBatch();

                    WorldGenerator TerrainGen = new SimplexTerrainGenerator(12345L);
                    world = new World(32, 10, 12345L, TerrainGen);
                    meshBuilder = new ChunkMeshBuilder(world);
                    chunkInstances = new ArrayList<>();

                    loadingCx = 0; loadingCz = 0; loadingCy = 0;
                    loadStep++;
                    break;

                case 1:
                    processNextChunk();

                    if (loadStep == 1) {
                        float chunkProgress = getLoadingProgress();
                        drawProgressBar(0.1f + 0.8f * chunkProgress);
                        return;
                    }
                    break;

                case 2:
                    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    camera.position.set(world.getWorldWidth() / 2f, 60f, world.getWorldWidth() / 2f);
                    camera.near = 1f; camera.far = 300f;
                    yaw = 0; pitch = -60f;
                    updateCameraDirection();
                    Gdx.input.setInputProcessor(this);

                    drawProgressBar(1.0f);

                    isLoading = false;
                    break;
            }
        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glClearColor(0.5f, 0.7f, 1.0f, 1.0f);

            handleMovement(Gdx.graphics.getDeltaTime());

            camera.update();
            modelBatch.begin(camera);
            modelBatch.render(chunkInstances, environment);
            modelBatch.end();
        }
    }

    private void drawProgressBar(float progress) {
        float barWidth = Gdx.graphics.getWidth() * 0.6f;
        float barHeight = 20f;
        float x = (Gdx.graphics.getWidth() - barWidth) / 2f;
        float y = (Gdx.graphics.getHeight() - barHeight) / 2f;

        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(x, y, barWidth, barHeight);

        shapeRenderer.setColor(0.1f, 0.8f, 0.2f, 1f);
        shapeRenderer.rect(x, y, barWidth * progress, barHeight);

        shapeRenderer.end();
    }

    private void handleMovement(float deltaTime) {
        if (!isMouseLocked) return;

        Vector3 forward = new Vector3(camera.direction).nor();
        Vector3 right = new Vector3(camera.direction).crs(camera.up).nor();

        forward.y = 0;
        forward.nor();

        Vector3 velocity = new Vector3();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.add(forward);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.sub(forward);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.sub(right);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.add(right);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) velocity.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) velocity.y -= 1;

        if (velocity.len() > 0) {
            velocity.nor().scl(walkSpeed * deltaTime);
            camera.position.add(velocity);
        }
    }

    private void updateCameraDirection() {
        camera.direction.set(
            MathUtils.sin(yaw * MathUtils.degreesToRadians) * MathUtils.cos(pitch * MathUtils.degreesToRadians),
            MathUtils.sin(pitch * MathUtils.degreesToRadians),
            -MathUtils.cos(yaw * MathUtils.degreesToRadians) * MathUtils.cos(pitch * MathUtils.degreesToRadians)
        ).nor();
        camera.up.set(0, 1, 0);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        for(ModelInstance mi : chunkInstances) {
            if(mi.model != null) mi.model.dispose();
        }
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            if (isMouseLocked) {
                unlockMouse();
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isMouseLocked && button == Buttons.LEFT) {
            lockMouse();
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!isMouseLocked) return false;

        int centerX = Gdx.graphics.getWidth() / 2;
        int centerY = Gdx.graphics.getHeight() / 2;

        float deltaX = screenX - centerX;
        float deltaY = screenY - centerY;

        yaw += deltaX * mouseSensitivity;
        pitch -= deltaY * mouseSensitivity;

        if (pitch > 89f) pitch = 89f;
        if (pitch < -89f) pitch = -89f;

        updateCameraDirection();

        Gdx.input.setCursorPosition(centerX, centerY);

        return true;
    }

    private void lockMouse() {
        isMouseLocked = true;
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    private void unlockMouse() {
        isMouseLocked = false;
        Gdx.input.setCursorCatched(false);
    }

    private void processNextChunk() {
        int processed = 0;

        while (processed < CHUNKS_PER_FRAME && loadStep == 1) {
            ChunkColumn column = world.getChunkColumnMap().get(new Int2Pos(loadingCx, loadingCz));
            if (column != null) {
                Chunk chunk = column.getChunk(loadingCy);
                if (chunk != null) {
                    ModelInstance instance = meshBuilder.buildChunkMesh(chunk);
                    if (instance != null) {
                        chunkInstances.add(instance);
                    }
                }
            }

            loadingCy++;
            if (loadingCy >= world.getWorldChunkHeight()) {
                loadingCy = 0;
                loadingCz++;
                if (loadingCz >= world.getWorldSize()) {
                    loadingCz = 0;
                    loadingCx++;
                    if (loadingCx >= world.getWorldSize()) {
                        loadStep++;
                    }
                }
            }

            processed++;
        }
    }

    private float getLoadingProgress() {
        int totalChunks = world.getWorldSize() * world.getWorldSize() * world.getWorldChunkHeight();
        if (totalChunks == 0) return 0;

        int currentCount = loadingCx * world.getWorldSize() * world.getWorldChunkHeight()
            + loadingCz * world.getWorldChunkHeight()
            + loadingCy;
        return (float)currentCount / totalChunks;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return mouseMoved(screenX, screenY); } // 拖拽时也转动视角
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
