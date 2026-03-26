package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CityScreen extends SkyScreen {

    private ShapeRenderer cityRenderer;

    // Dynamic layout
    private float groundHeight;
    private float[] buildingX, buildingY, buildingW, buildingH;
    private float[] lampX;

    public CityScreen(GameMaster gameMaster) {
        super(gameMaster);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        cityRenderer = new ShapeRenderer();
        calculateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        System.out.println("CityScreen loaded.");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        calculateLayout(width, height);
    }

    private void calculateLayout(float w, float h) {
        groundHeight = h * 0.15f;

        buildingX = new float[]{ w * 0.05f, w * 0.25f, w * 0.45f, w * 0.68f };
        buildingW = new float[]{ w * 0.14f, w * 0.12f, w * 0.18f, w * 0.14f };
        buildingH = new float[]{ h * 0.55f, h * 0.65f, h * 0.60f, h * 0.50f };
        buildingY = new float[]{ groundHeight, groundHeight, groundHeight, groundHeight };
        lampX     = new float[]{ w * 0.04f, w * 0.42f, w * 0.76f };
    }

    @Override
    public void render() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        renderSky();
        backClouds(h);

        cityRenderer.setProjectionMatrix(camera.combined);
        cityRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Ground
        cityRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        cityRenderer.rect(0, 0, w, groundHeight);

        // Buildings
        for (int i = 0; i < 4; i++) {
            drawBuilding(buildingX[i], buildingY[i], buildingW[i], buildingH[i]);
        }

        // Lamp posts
        for (float lx : lampX) {
            drawLampPost(lx, groundHeight, h);
        }

        cityRenderer.end();

        frontClouds(h);
        super.render();
    }

    private void drawBuilding(float x, float y, float width, float height) {
        cityRenderer.setColor(Color.DARK_GRAY);
        cityRenderer.rect(x, y, width, height);

        float winW = width  * 0.15f;
        float winH = height * 0.06f;
        float gapX = width  * 0.22f;
        float gapY = height * 0.08f;

        cityRenderer.setColor(Color.YELLOW);
        for (float wx = x + width * 0.10f; wx < x + width - winW; wx += gapX) {
            for (float wy = y + height * 0.05f; wy < y + height - winH; wy += gapY) {
                cityRenderer.rect(wx, wy, winW, winH);
            }
        }
    }

    private void drawLampPost(float x, float y, float h) {
        float poleH  = h * 0.12f;
        float poleW  = h * 0.008f;
        float armLen = h * 0.03f;

        cityRenderer.setColor(Color.BLACK);
        cityRenderer.rect(x, y, poleW, poleH);
        cityRenderer.rect(x + poleW, y + poleH * 0.88f, armLen, poleW);

        cityRenderer.setColor(Color.YELLOW);
        cityRenderer.circle(x + poleW + armLen, y + poleH * 0.89f, poleW * 1.5f);
    }

    @Override
    public void onUnload() {
        if (cityRenderer != null) cityRenderer.dispose();
        super.onUnload();
    }
}
