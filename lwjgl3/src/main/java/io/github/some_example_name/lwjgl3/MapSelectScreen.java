package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * MapSelectScreen — Allows the player to choose a map theme before playing.
 *
 * <h3>Screen flow</h3>
 * StartScreen → <b>MapSelectScreen</b> → ActivityScreen
 *
 * <h3>Layout</h3>
 * Four buttons are displayed vertically, one per {@link MapTheme}.
 * Clicking a button sets the chosen theme on the {@link GameMaster}'s
 * {@link BackgroundManager} and transitions to {@link ActivityScreen}.
 *
 * <h3>SOLID</h3>
 * <ul>
 *   <li><b>SRP:</b> This screen only handles theme selection UI.</li>
 *   <li><b>OCP:</b> New themes are added to the {@link MapTheme} enum —
 *       this screen iterates the enum values automatically.</li>
 * </ul>
 */
public class MapSelectScreen extends Screen {

    // ── Layout constants ──────────────────────────────────────────────────────

    private static final float BUTTON_WIDTH  = 250f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_GAP    = 20f;

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final GameMaster gameMaster;

    // ── Rendering resources ───────────────────────────────────────────────────

    private SpriteBatch   batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont    font;
    private GlyphLayout   layout;

    // ── Button layout ─────────────────────────────────────────────────────────

    /** Precomputed Y positions for each theme button (parallel to MapTheme.values()). */
    private float[] buttonY;

    // ── Constructor ───────────────────────────────────────────────────────────

    public MapSelectScreen(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onLoad() {
        super.onLoad();
        batch         = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font          = new BitmapFont();
        font.getData().setScale(1.4f);
        layout        = new GlyphLayout();

        // Compute button Y positions — centred vertically.
        MapTheme[] themes = MapTheme.values();
        float totalH = themes.length * BUTTON_HEIGHT + (themes.length - 1) * BUTTON_GAP;
        float startY = (Gdx.graphics.getHeight() + totalH) / 2f - BUTTON_HEIGHT;
        buttonY = new float[themes.length];
        for (int i = 0; i < themes.length; i++) {
            buttonY[i] = startY - i * (BUTTON_HEIGHT + BUTTON_GAP);
        }

        System.out.println("MapSelectScreen loaded.");
    }

    @Override
    public void update(float deltaTime) {
        IInputProvider io = gameMaster.getInputProvider();
        float     mx     = io.getMouseX();
        float     my     = io.getMouseY();
        float     btnX   = (Gdx.graphics.getWidth() - BUTTON_WIDTH) / 2f;
        MapTheme[] themes = MapTheme.values();

        if (io.isMouseButtonJustPressed(0)) {
            for (int i = 0; i < themes.length; i++) {
                if (mx >= btnX && mx <= btnX + BUTTON_WIDTH &&
                    my >= buttonY[i] && my <= buttonY[i] + BUTTON_HEIGHT) {
                    // Set the chosen theme and start the game.
                    MapTheme selectedTheme = themes[i];
                    gameMaster.getBackgroundManager().setTheme(selectedTheme);
                    gameMaster.getSoundManager().playSound("sfx1");

                    if (selectedTheme == MapTheme.BEACH) {
                        gameMaster.getScreenManager().setScreen(
                            new BeachScreen(gameMaster));
                    }
                    else if (selectedTheme == MapTheme.CITY) {
                        gameMaster.getScreenManager().setScreen(
                            new CityScreen(gameMaster));
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        MapTheme[] themes = MapTheme.values();
        float totalH = themes.length * BUTTON_HEIGHT + (themes.length - 1) * BUTTON_GAP;
        float startY = (height + totalH) / 2f - BUTTON_HEIGHT;
        buttonY = new float[themes.length];
        for (int i = 0; i < themes.length; i++) {
            buttonY[i] = startY - i * (BUTTON_HEIGHT + BUTTON_GAP);
        }

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void render() {
        float     btnX   = (Gdx.graphics.getWidth() - BUTTON_WIDTH) / 2f;
        MapTheme[] themes = MapTheme.values();

        // ── Title ────────────────────────────────────────────────────────────
        batch.begin();
        font.setColor(Color.WHITE);
        layout.setText(font, "SELECT MAP");
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() / 2f + 180f);
        batch.end();

        // ── Buttons (shapes) ─────────────────────────────────────────────────
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < themes.length; i++) {
            // Button background — use the theme's clean colour.
            Color c = themes[i].getCleanColor();
            shapeRenderer.setColor(c.r * 0.7f, c.g * 0.7f, c.b * 0.7f, 1f);
            shapeRenderer.rect(btnX, buttonY[i], BUTTON_WIDTH, BUTTON_HEIGHT);
        }
        shapeRenderer.end();

        // ── Button labels ─────────────────────────────────────────────────────
        batch.begin();
        for (int i = 0; i < themes.length; i++) {
            font.setColor(Color.WHITE);
            layout.setText(font, themes[i].getDisplayName());
            font.draw(batch, layout,
                btnX + (BUTTON_WIDTH  - layout.width)  / 2f,
                buttonY[i] + (BUTTON_HEIGHT + layout.height) / 2f);
        }
        batch.end();
    }

    @Override
    public void onUnload() {
        if (batch         != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font          != null) font.dispose();
        super.onUnload();
    }
}
