package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SkyScreen extends ActivityScreen {

    protected float skyTimer = 0f;
    protected ShapeRenderer skyRenderer;

    //clouds
    protected float cloud1x, cloud2x, cloud3x, cloud4x;

    /*  light blue - day
        reddish - sunset
        dark blue - night
     */
    protected Color dayColor    = new Color(0.53f,0.81f,0.98f,1f);
    protected Color sunsetColor = new Color(1f,0.35f,0.25f,1f);
    protected Color nightColor  = new Color(0.05f,0.05f,0.2f,1f);

    public SkyScreen(GameMaster gameMaster) {
        super(gameMaster);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        skyRenderer = new ShapeRenderer();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        cloud1x = 40;
        cloud2x = 600;
        cloud3x = 300;
        cloud4x = 30;
    }

    @Override
    public void update(float deltaTime) {

        //sky cycle speed
        skyTimer += deltaTime * 0.05f;

        if (skyTimer > 1f)
            skyTimer = 0f;

        float w = Gdx.graphics.getWidth();

        //moving clouds
        cloud1x += 20 * deltaTime;
        cloud2x += 20 * deltaTime;
        cloud3x += 15 * deltaTime;
        cloud4x += 20 * deltaTime;

        if (cloud1x > w) cloud1x = -100;
        if (cloud2x > w) cloud2x = -150;
        if (cloud3x > w) cloud3x = -100;
        if (cloud4x > w) cloud4x = -150;

        super.update(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cloud1x = 40;
        cloud2x = width * 0.6f;
        cloud3x = width * 0.3f;
        cloud4x = 30;
    }

    protected Color getCurrentSkyColor() {

        if (skyTimer < 0.33f) {
            return dayColor.cpy().lerp(sunsetColor, skyTimer / 0.33f);
        }
        else if (skyTimer < 0.66f) {
            return sunsetColor.cpy().lerp(nightColor, (skyTimer - 0.33f) / 0.33f);
        }
        else {
            return nightColor.cpy().lerp(dayColor, (skyTimer - 0.66f) / 0.34f);
        }
    }

    protected void backClouds(float h){

        skyRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //cloud1x
        drawCloud(cloud1x + 60, h - 120);

        //cloud2x
        drawCloud(cloud2x, h - 70);
        skyRenderer.end();
    }

    protected void frontClouds(float h){
        skyRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //cloud3x
        drawCloud(cloud3x + 20, h - 250);

        //cloud4x
        drawCloud(cloud4x + 500, h - 200);
        skyRenderer.end();
    }

    protected void renderSky() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Color sky = getCurrentSkyColor();

        skyRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //sky
        skyRenderer.setColor(sky);
        skyRenderer.rect(0,0,w,h);

        skyRenderer.end();
    }

    private void drawCloud(float x, float y) {

        skyRenderer.setColor(Color.WHITE);

        skyRenderer.circle(x, y, 25);
        skyRenderer.circle(x + 30, y + 10, 30);
        skyRenderer.circle(x + 60, y, 25);
    }

    @Override
    public void onUnload() {
        if (skyRenderer != null)
            skyRenderer.dispose();

        super.onUnload();
    }

}
