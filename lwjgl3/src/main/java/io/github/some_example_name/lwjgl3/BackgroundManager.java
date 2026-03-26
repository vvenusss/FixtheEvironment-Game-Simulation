package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;

/**
 * BackgroundManager — Manages the dynamic background colour of the game world.
 *
 * <h3>Responsibility (SRP)</h3>
 * This class has exactly one job: translate the current pollution level
 * (0 = clean, 100 = fully polluted) into a background {@link Color} that
 * is passed to {@code Gdx.gl.glClearColor()} each frame. It knows nothing
 * about entities, input, or scoring.
 *
 * <h3>Colour interpolation</h3>
 * The background linearly interpolates between the theme's clean colour
 * (at pollution = 0) and a fixed dark smoggy colour (at pollution = 100).
 * This creates a visible, continuous environmental feedback loop.
 *
 * <h3>Usage</h3>
 * <pre>
 *   BackgroundManager bg = new BackgroundManager(MapTheme.CITY);
 *   bg.setPollutionLevel(60f);
 *   Color c = bg.getBackgroundColor();
 *   Gdx.gl.glClearColor(c.r, c.g, c.b, 1f);
 * </pre>
 */
public class BackgroundManager {

    // ── Constants ─────────────────────────────────────────────────────────────

    /**
     * The background colour representing maximum pollution (100%).
     * A dark, smoggy brownish-grey used across all map themes.
     */
    private static final Color POLLUTED_COLOR = new Color(0.20f, 0.18f, 0.15f, 1f);

    /** Minimum pollution level (fully clean). */
    public static final float MIN_POLLUTION = 0f;

    /** Maximum pollution level (fully polluted). */
    public static final float MAX_POLLUTION = 100f;

    // ── State ─────────────────────────────────────────────────────────────────

    /** The active map theme — determines the clean-state colour. */
    private MapTheme theme;

    /**
     * Current pollution level in the range [0, 100].
     * 0 = clean environment; 100 = maximum pollution.
     */
    private float pollutionLevel;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a BackgroundManager for the given map theme.
     * Pollution starts at 50% (mid-range, partially polluted).
     *
     * @param theme The {@link MapTheme} for this game session.
     */
    public BackgroundManager(MapTheme theme) {
        this.theme          = theme;
        this.pollutionLevel = 50f; // start mid-range
    }

    // ── Core logic ────────────────────────────────────────────────────────────

    /**
     * Returns the background colour for the current pollution level.
     *
     * The colour is a linear interpolation between the theme's clean colour
     * (pollution = 0) and the universal polluted colour (pollution = 100).
     *
     * @return A new {@link Color} instance representing the current background.
     */
    public Color getBackgroundColor() {
        // t = 0 → clean colour; t = 1 → polluted colour
        float t = pollutionLevel / MAX_POLLUTION;
        Color clean    = theme.getCleanColor();
        Color polluted = POLLUTED_COLOR;

        return new Color(
            lerp(clean.r, polluted.r, t),
            lerp(clean.g, polluted.g, t),
            lerp(clean.b, polluted.b, t),
            1f
        );
    }

    // ── Pollution level control ───────────────────────────────────────────────

    /**
     * Increases the pollution level by the given amount, clamped to
     * [{@link #MIN_POLLUTION}, {@link #MAX_POLLUTION}].
     *
     * @param amount Positive value to add to the pollution level.
     */
    public void increasePollution(float amount) {
        pollutionLevel = Math.min(MAX_POLLUTION, pollutionLevel + amount);
    }

    /**
     * Decreases the pollution level by the given amount, clamped to
     * [{@link #MIN_POLLUTION}, {@link #MAX_POLLUTION}].
     *
     * @param amount Positive value to subtract from the pollution level.
     */
    public void decreasePollution(float amount) {
        pollutionLevel = Math.max(MIN_POLLUTION, pollutionLevel - amount);
    }

    /**
     * Sets the pollution level directly, clamped to
     * [{@link #MIN_POLLUTION}, {@link #MAX_POLLUTION}].
     *
     * @param level New pollution level.
     */
    public void setPollutionLevel(float level) {
        pollutionLevel = Math.max(MIN_POLLUTION, Math.min(MAX_POLLUTION, level));
    }

    /** Returns the current pollution level in [0, 100]. */
    public float getPollutionLevel() { return pollutionLevel; }

    // ── Theme control ─────────────────────────────────────────────────────────

    /**
     * Switches the active map theme. The background colour will immediately
     * reflect the new theme's clean colour.
     *
     * @param theme The new {@link MapTheme}.
     */
    public void setTheme(MapTheme theme) { this.theme = theme; }

    /** Returns the currently active {@link MapTheme}. */
    public MapTheme getTheme() { return theme; }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Linear interpolation between a and b by factor t ∈ [0, 1]. */
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
