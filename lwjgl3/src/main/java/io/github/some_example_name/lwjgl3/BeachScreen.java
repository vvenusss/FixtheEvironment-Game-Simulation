package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BeachScreen extends SkyScreen {
    private ShapeRenderer shapeRenderer;

    public BeachScreen(GameMaster gameMaster) {
        super(gameMaster);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        shapeRenderer = new ShapeRenderer();

        System.out.println("beach screen loaded.");

    }

    @Override
    public void render() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        renderSky();
        backClouds(h);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        //ocean
        shapeRenderer.setColor(0.1f, 0.4f, 0.8f, 1f);
        shapeRenderer.rect(0, 100, w, 200);

        //sand
        shapeRenderer.setColor(0.95f, 0.85f, 0.5f, 1f);
        shapeRenderer.rect(0, 0, w, 120);

        //palm tree
        drawPalmTree(120, 50);
        drawPalmTree(300, 120);
        drawPalmTree(500, 90);
        drawPalmTree(70, 110);
        drawPalmTree(600, 30);

        shapeRenderer.end();
        frontClouds(h);

        super.render();
    }

    private void drawPalmTree(float x, float y) {

        // trunk
        shapeRenderer.setColor(0.55f, 0.27f, 0.07f, 1f);

        shapeRenderer.rect(x, y, 10, 30);
        shapeRenderer.rect(x + 9, y + 70, 10, 30);
        shapeRenderer.rect(x + 6, y + 50, 10, 30);
        shapeRenderer.rect(x + 3, y + 30, 10, 30);

        // leaves
        shapeRenderer.setColor(0.1f, 0.6f, 0.1f, 1f);

        shapeRenderer.triangle(x + 8, y + 120, x - 40, y + 100, x + 20, y + 100);
        shapeRenderer.triangle(x + 8, y + 120, x + 60, y + 100, x + 20, y + 100);
        shapeRenderer.triangle(x + 8, y + 120, x - 40, y + 140, x + 20, y + 100);
        shapeRenderer.triangle(x + 8, y + 120, x + 40, y + 140, x + 20, y + 100);
    }

    public void onUnload(){
        shapeRenderer.dispose();
        super.onUnload();
    }
}
