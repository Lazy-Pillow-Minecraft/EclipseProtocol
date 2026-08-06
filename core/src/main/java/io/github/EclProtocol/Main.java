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
import io.github.EclProtocol.util.math.Mth;
import io.github.EclProtocol.worldgen.World;
import io.github.EclProtocol.worldgen.generator.SimplexTerrainGenerator;
import io.github.EclProtocol.worldgen.generator.WorldGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class Main extends ApplicationAdapter implements InputProcessor {
    private final int CHUNKS_PER_FRAME = 1000;
    private final int renderDistance = 16;

    private final List<ChunkColumnDist> candidateColumns = new ArrayList<>();
    private final Set<Int2Pos> generatedColumns = new HashSet<>();
    private final List<ChunkColumn> bakeQueue = new ArrayList<>();
    private final List<ChunkMeshBuilder.ChunkMeshData> bakedResults = new ArrayList<>();
    private final float walkSpeed = 30f;
    private final float mouseSensitivity = 0.1f;
    private Thread bakerThread;
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
    private float yaw = 0f;
    private float pitch = 0f;

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public void create() {
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
        startBakerThread();
    }

    @Override
    public void resize(int width, int height) {
        if (camera == null) return;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    private void startBakerThread() {
        bakerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                ChunkColumn columnToBake;

                synchronized (bakeQueue) {
                    while (bakeQueue.isEmpty()) {
                        try {
                            bakeQueue.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    columnToBake = bakeQueue.remove(0);
                }

                if (columnToBake != null) {
                    for (int cy = 0; cy < world.getWorldChunkHeight(); cy++) {
                        Chunk chunk = columnToBake.getChunk(cy);
                        if (chunk != null) {
                            if (meshBuilder != null) {
                                ChunkMeshBuilder.ChunkMeshData data = meshBuilder.buildChunkMeshData(chunk);
                                if (data != null) {
                                    synchronized (bakedResults) {
                                        bakedResults.add(data);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        bakerThread.setDaemon(true);
        bakerThread.start();
    }

    @Override
    public void render() {
        if (isLoading) {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            switch (loadStep) {
                case 0:
                    environment = new Environment();
                    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.6f, 0.8f, 1f));
                    environment.add(new DirectionalLight().set(1.0f, 0.8f, 0.65f, -1f, -2.5f, 1.3f));
                    modelBatch = new ModelBatch();
                    WorldGenerator TerrainGen = new SimplexTerrainGenerator(12345L);
                    world = new World(128, 15, 12345L, TerrainGen);
                    meshBuilder = new ChunkMeshBuilder(world);
                    chunkInstances = new ArrayList<>();
                    generatedColumns.clear();
                    candidateColumns.clear();
                    loadStep++;
                    break;
                case 1:
                    processNextChunk();

                    float total = (float) (Math.pow(renderDistance * 2, 2) * world.getWorldChunkHeight());
                    float progress = Math.min(1.0f, (float) chunkInstances.size() / total);
                    drawProgressBar(0.1f + 0.8f * progress);

                    if (candidateColumns.isEmpty() && !chunkInstances.isEmpty()) {
                        loadStep++;
                    }
                    return;
                case 2:
                    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    camera.position.set(world.getWorldWidth() / 2f, 100, world.getWorldWidth() / 2f);
                    camera.near = 1f;
                    camera.far = 1000F;
                    yaw = 0;
                    pitch = -60f;
                    updateCameraDirection();
                    Gdx.input.setInputProcessor(this);
                    drawProgressBar(1.0f);
                    isLoading = false;
                    break;
            }
        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glClearColor(0.5f, 0.7f, 1.0f, 1f);

            handleMovement(Gdx.graphics.getDeltaTime());

            if (System.currentTimeMillis() % 500 < 20) {
                processNextChunk();
                unloadDistantChunks(camera.position);
            }

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

        float prevX = camera.position.x;
        float prevZ = camera.position.z;

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
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
            velocity.y -= 1;

        if (velocity.len() > 0) {
            velocity.nor().scl(walkSpeed * deltaTime);
            camera.position.add(velocity);
        }

        float worldWidth = world.getWorldWidth();
        if (worldWidth > 0) {
            camera.position.x = Mth.loopCoord(camera.position.x, worldWidth);
            camera.position.z = Mth.loopCoord(camera.position.z, worldWidth);
        }

        if (worldWidth > 0 && (Math.abs(camera.position.x - prevX) > worldWidth / 2.0f || Math.abs(camera.position.z - prevZ) > worldWidth / 2.0f)) {
            unloadDistantChunks(camera.position);
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

    private void unloadDistantChunks(Vector3 centerPos) {
        float maxDist = (renderDistance + 1) * Chunk.CHUNK_SIZE;
        float maxDistSq = maxDist * maxDist;
        float worldWidth = world.getWorldWidth();

        for (int i = chunkInstances.size() - 1; i >= 0; i--) {
            ModelInstance instance = chunkInstances.get(i);
            if (instance.userData instanceof Int2Pos) {
                Int2Pos pos = (Int2Pos) instance.userData;

                float blockX = pos.x * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE / 2.0f;
                float blockZ = pos.y * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE / 2.0f;

                float dx = blockX - centerPos.x;
                float dz = blockZ - centerPos.z;

                if (Math.abs(dx) > worldWidth / 2.0f) dx -= Math.signum(dx) * worldWidth;
                if (Math.abs(dz) > worldWidth / 2.0f) dz -= Math.signum(dz) * worldWidth;

                if (dx * dx + dz * dz > maxDistSq) {
                    instance.model.dispose();
                    chunkInstances.remove(i);
                    generatedColumns.remove(pos);
                } else {

                    float renderX = 0;
                    float renderZ = 0;

                    // 处理 X 轴
                    float xOffset = (blockX - centerPos.x);
                    if (Math.abs(xOffset) > worldWidth / 2.0f) {
                        if (xOffset > 0) {
                            renderX -= worldWidth;
                        } else {
                            renderX += worldWidth;
                        }
                    }

                    // 处理 Z 轴
                    float zOffset = (blockZ - centerPos.z);
                    if (Math.abs(zOffset) > worldWidth / 2.0f) {
                        if (zOffset > 0) {
                            renderZ -= worldWidth;
                        } else {
                            renderZ += worldWidth;
                        }
                    }

                    instance.transform.setToTranslation(renderX, 0, renderZ);
                }
            }
        }
    }

    private void processNextChunk() {
        Vector3 centerPos = (camera == null) ? new Vector3(world.getWorldWidth() / 2f, 0, world.getWorldWidth() / 2f) : camera.position;

        if (candidateColumns.isEmpty()) {
            fillCandidateColumns(centerPos);
        }

        int submitted = 0;
        while (submitted < CHUNKS_PER_FRAME && !candidateColumns.isEmpty()) {
            ChunkColumnDist best = candidateColumns.get(0);
            candidateColumns.remove(0);

            if (!generatedColumns.contains(best.pos)) {
                generatedColumns.add(best.pos);
                ChunkColumn column = world.getChunkColumnMap().get(best.pos);
                if (column != null) {
                    synchronized (bakeQueue) {
                        bakeQueue.add(column);
                        bakeQueue.notify();
                    }
                }
            }
            submitted++;
        }

        synchronized (bakedResults) {
            if (!bakedResults.isEmpty()) {
                int limit = Math.min(bakedResults.size(), CHUNKS_PER_FRAME);
                for (int i = 0; i < limit; i++) {
                    ChunkMeshBuilder.ChunkMeshData data = bakedResults.remove(0);
                    ModelInstance instance = meshBuilder.createModelFromData(data);
                    if (instance != null) {
                        chunkInstances.add(instance);
                    }
                }
            }
        }
    }

    private void fillCandidateColumns(Vector3 centerPos) {
        candidateColumns.clear();
        int worldSizeChunks = world.getWorldSize();
        float maxDist = renderDistance * Chunk.CHUNK_SIZE;
        float maxDistSq = maxDist * maxDist;
        float worldWidth = world.getWorldWidth();

        for (int cx = 0; cx < worldSizeChunks; cx++) {
            for (int cz = 0; cz < worldSizeChunks; cz++) {
                Int2Pos pos = new Int2Pos(cx, cz);
                if (generatedColumns.contains(pos)) continue;

                float blockX = cx * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE / 2.0f;
                float blockZ = cz * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE / 2.0f;
                float dx = blockX - centerPos.x;
                float dz = blockZ - centerPos.z;

                if (Math.abs(dx) > worldWidth / 2.0f) {
                    dx -= Math.signum(dx) * worldWidth;
                }
                if (Math.abs(dz) > worldWidth / 2.0f) {
                    dz -= Math.signum(dz) * worldWidth;
                }

                if (dx * dx + dz * dz < maxDistSq) {
                    candidateColumns.add(new ChunkColumnDist(pos, dx * dx + dz * dz));
                }
            }
        }
        candidateColumns.sort((a, b) -> Float.compare(a.distSq, b.distSq));
    }

    @Override
    public void dispose() {
        if (bakerThread != null) bakerThread.interrupt();
        modelBatch.dispose();
        for (ModelInstance mi : chunkInstances) {
            if (mi.model != null) mi.model.dispose();
        }
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE && isMouseLocked) unlockMouse();
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isMouseLocked && button == Buttons.LEFT) lockMouse();
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

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private static class ChunkColumnDist {
        Int2Pos pos;
        float distSq;

        public ChunkColumnDist(Int2Pos pos, float distSq) {
            this.pos = pos;
            this.distSq = distSq;
        }
    }
}
