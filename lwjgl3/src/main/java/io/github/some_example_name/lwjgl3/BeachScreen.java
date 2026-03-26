package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BeachScreen extends SkyScreen {

    private ShapeRenderer shapeRenderer;

    // Dynamic layout
    private float sandHeight;
    private float oceanY, oceanHeight;
    private float[] treeX, treeY;

    public BeachScreen(GameMaster gameMaster) {
        super(gameMaster);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        shapeRenderer = new ShapeRenderer();
        calculateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        System.out.println("beach screen loaded.");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        calculateLayout(width, height);
    }

    private void calculateLayout(float w, float h) {
        sandHeight  = h * 0.15f;
        oceanY      = h * 0.13f;
        oceanHeight = h * 0.25f;

        // 5 palm trees — positions relative to screen
        treeX = new float[]{ w * 0.12f, w * 0.30f, w * 0.50f, w * 0.07f, w * 0.60f };
        treeY = new float[]{ h * 0.06f, h * 0.15f, h * 0.11f, h * 0.14f, h * 0.04f };
    }

    @Override
    public void render() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        renderSky();
        backClouds(h);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Ocean
        shapeRenderer.setColor(0.1f, 0.4f, 0.8f, 1f);
        shapeRenderer.rect(0, oceanY, w, oceanHeight);

        // Sand
        shapeRenderer.setColor(0.95f, 0.85f, 0.5f, 1f);
        shapeRenderer.rect(0, 0, w, sandHeight);

        // Palm trees
        for (int i = 0; i < treeX.length; i++) {
            drawPalmTree(treeX[i], treeY[i], h);
        }

        shapeRenderer.end();
        frontClouds(h);
        super.render();
    }

    private void drawPalmTree(float x, float y, float h) {
        // Scale trunk and leaves relative to screen height
        float scale  = h / 480f;
        float trunkW = 10f * scale;
        float segH   = 30f * scale;

        // Trunk segments
        shapeRenderer.setColor(0.55f, 0.27f, 0.07f, 1f);
        shapeRenderer.rect(x,              y,                trunkW, segH);
        shapeRenderer.rect(x + 3 * scale,  y + segH,         trunkW, segH);
        shapeRenderer.rect(x + 6 * scale,  y + segH * 2,     trunkW, segH);
        shapeRenderer.rect(x + 9 * scale,  y + segH * 3,     trunkW, segH);

        // Leaves
        float tx  = x + 8  * scale;
        float ty  = y + segH * 4;
        float lsp = 40f * scale;
        float lsh = 20f * scale;

        shapeRenderer.setColor(0.1f, 0.6f, 0.1f, 1f);
        shapeRenderer.triangle(tx, ty, tx - lsp, ty - lsh, tx + lsp * 0.3f, ty - lsh);
        shapeRenderer.triangle(tx, ty, tx + lsp, ty - lsh, tx + lsp * 0.3f, ty - lsh);
        shapeRenderer.triangle(tx, ty, tx - lsp, ty + lsh, tx + lsp * 0.3f, ty - lsh);
        shapeRenderer.triangle(tx, ty, tx + lsp, ty + lsh, tx + lsp * 0.3f, ty - lsh);
    }

    @Override
    public void onUnload() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        super.onUnload();
    }
}
