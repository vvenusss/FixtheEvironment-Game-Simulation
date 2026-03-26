package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * StartScreen — The title screen for FutureX: Fix the Environment.
 *
 * <h3>Changes from Part 1</h3>
 * The "START" button now navigates to {@link MapSelectScreen} so the player
 * can choose a map theme before the game begins. All other logic is unchanged.
 *
 * <h3>Input</h3>
 * All input goes through {@link InputProvider} — no direct {@code Gdx.input} calls.
 */
public class StartScreen extends Screen {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final GameMaster gameMaster;

    // ── Rendering resources ───────────────────────────────────────────────────

    private SpriteBatch batch;
    private BitmapFont  font;
    private GlyphLayout layout;

    // ── Button layout ─────────────────────────────────────────────────────────

    private float buttonX, buttonY, buttonWidth, buttonHeight;

    // ── Constructor ───────────────────────────────────────────────────────────

    public StartScreen(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onLoad() {
        super.onLoad();
        batch  = new SpriteBatch();
        font   = new BitmapFont();
        font.getData().setScale(1.5f);
        layout = new GlyphLayout();

        buttonWidth  = 200f;
        buttonHeight = 50f;
        buttonX = (Gdx.graphics.getWidth()  - buttonWidth)  / 2f;
        buttonY = (Gdx.graphics.getHeight() / 2f) - 80f;

        System.out.println("StartScreen loaded.");
    }

    @Override
    public void update(float deltaTime) {
        IInputProvider io = gameMaster.getInputProvider();
        if (io.isMouseButtonJustPressed(0)) {
            float mx = io.getMouseX();
            float my = io.getMouseY();
            if (mx >= buttonX && mx <= buttonX + buttonWidth &&
                my >= buttonY && my <= buttonY + buttonHeight) {
                gameMaster.getSoundManager().playSound("sfx1");
                // Navigate to map selection before starting the game.
                gameMaster.getScreenManager().setScreen(new MapSelectScreen(gameMaster));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        buttonX = (width  - buttonWidth)  / 2f;
        buttonY = (height / 2f) - 80f;

        batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);
    }

    @Override
    public void render() {
        batch.begin();

        // Title
        font.setColor(Color.GREEN);
        layout.setText(font, "FutureX: Fix the Environment");
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() / 2f + 80f);

        // Subtitle
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        layout.setText(font, "Collect green solutions. Reduce pollution. Save the city.");
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() / 2f + 30f);
        font.getData().setScale(1.5f);

        // START button
        font.setColor(Color.YELLOW);
        layout.setText(font, "START");
        font.draw(batch, layout,
            buttonX + (buttonWidth  - layout.width)  / 2f,
            buttonY + (buttonHeight + layout.height) / 2f);

        batch.end();
    }

    @Override
    public void onUnload() {
        if (batch  != null) batch.dispose();
        if (font   != null) font.dispose();
        super.onUnload();
    }
}
