package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CityScreen extends SkyScreen {

    private ShapeRenderer cityRenderer;

    public CityScreen(GameMaster gameMaster) {
        super(gameMaster);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        cityRenderer = new ShapeRenderer();
        System.out.println("CityScreen loaded.");
    }

    @Override
    public void render() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        renderSky();
        backClouds(h);

        cityRenderer.begin(ShapeRenderer.ShapeType.Filled);


        //ground
        cityRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        cityRenderer.rect(0, 0, w, 120);

        //buildings
        drawBuilding(80, 120, 120, 320);
        drawBuilding(230, 120, 100, 400);
        drawBuilding(360, 120, 150, 350);
        drawBuilding(540, 120, 110, 280);

        drawLampPost(60, 120);
        drawLampPost(350, 120);
        drawLampPost(580, 120);

        cityRenderer.end();

        frontClouds(h);

        // Render gameplay objects + UI
        super.render();
    }

    private void drawBuilding(float x, float y, float width, float height) {

        //building
        cityRenderer.setColor(Color.DARK_GRAY);
        cityRenderer.rect(x, y, width, height);

        //windows
        cityRenderer.setColor(Color.YELLOW);

        for (float wx = x + 10; wx < x + width - 10; wx += 25) {
            for (float wy = y + 20; wy < y + height - 20; wy += 35) {
                cityRenderer.rect(wx, wy, 12, 18);
            }
        }
    }

    private void drawLampPost(float x, float y) {

        //pole
        cityRenderer.setColor(Color.BLACK);
        cityRenderer.rect(x, y, 6, 90);

        //arm
        cityRenderer.rect(x + 6, y + 80, 20, 4);

        //lamp light
        cityRenderer.setColor(Color.YELLOW);
        cityRenderer.circle(x + 28, y + 82, 6);
    }

    @Override
    public void onUnload() {
        if (cityRenderer != null) cityRenderer.dispose();
        super.onUnload();
    }
}
