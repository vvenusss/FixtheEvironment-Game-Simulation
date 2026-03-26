package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * PollutionBar — Renders the pollution level HUD bar.
 *
 * <h3>Responsibility (SRP)</h3>
 * This class is a pure rendering component. It reads the pollution level
 * from {@link BackgroundManager} and draws the HUD bar. It does not own
 * or modify the pollution level — that is {@link BackgroundManager}'s job.
 *
 * <h3>Win / Lose conditions (driven by {@link ActivityScreen})</h3>
 * <ul>
 *   <li>Pollution reaches 100 → game over (lose).</li>
 *   <li>Pollution reaches 0   → game over (win).</li>
 * </ul>
 *
 * <h3>HUD position</h3>
 * Positioned at the top-left of the screen (x = 10).
 */
public class PollutionBar {

    // ── HUD layout ────────────────────────────────────────────────────────────

    private static final float BAR_X      = 10f;   // top-left
    private static final float BAR_HEIGHT = 16f;
    private static final float BAR_WIDTH  = 200f;

    // ── Rendering ─────────────────────────────────────────────────────────────

    /**
     * Renders the pollution bar on the HUD.
     *
     * @param sr         The active {@link ShapeRenderer} (FILLED mode).
     * @param bgManager  The {@link BackgroundManager} providing the pollution level.
     * @param screenH    Screen height in pixels.
     */
    public void renderBar(ShapeRenderer sr, BackgroundManager bgManager, float screenH) {
        float barY      = screenH - BAR_HEIGHT - 30f;
        float pollution = bgManager.getPollutionLevel();
        float ratio     = pollution / BackgroundManager.MAX_POLLUTION;

        // Background
        sr.setColor(Color.DARK_GRAY);
        sr.rect(BAR_X, barY, BAR_WIDTH, BAR_HEIGHT);

        // Pollution fill — green (clean) → red (polluted)
        sr.setColor(new Color(ratio, 1f - ratio, 0f, 1f));
        sr.rect(BAR_X, barY, BAR_WIDTH * ratio, BAR_HEIGHT);

        // White border
        sr.setColor(Color.WHITE);
        sr.rect(BAR_X - 1,              barY - 1,              BAR_WIDTH + 2, 1);
        sr.rect(BAR_X - 1,              barY + BAR_HEIGHT,     BAR_WIDTH + 2, 1);
        sr.rect(BAR_X - 1,              barY,                  1,             BAR_HEIGHT);
        sr.rect(BAR_X + BAR_WIDTH,      barY,                  1,             BAR_HEIGHT);
    }

    /**
     * Renders the "Pollution" label above the bar.
     *
     * @param batch     The active {@link SpriteBatch}.
     * @param font      The font to use.
     * @param bgManager The {@link BackgroundManager} providing the pollution level.
     * @param screenH   Screen height in pixels.
     */
    public void renderLabel(SpriteBatch batch, BitmapFont font,
                            BackgroundManager bgManager, float screenH) {
        float labelY = screenH - 32f;
        font.setColor(Color.WHITE);
        font.draw(batch,
            String.format("Pollution: %d%%", (int) bgManager.getPollutionLevel()),
            BAR_X, labelY);
    }
}
