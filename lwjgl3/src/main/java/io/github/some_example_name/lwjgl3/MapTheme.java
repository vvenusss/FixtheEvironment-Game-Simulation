package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;

/**
 * MapTheme — Enumerates the four playable map environments in FutureX.
 *
 * Each theme carries a base background colour (the "clean" state of that
 * environment) and a display name shown on the map selection screen.
 * {@link BackgroundManager} blends from the polluted colour toward the
 * theme's clean colour as the city health improves.
 */
public enum MapTheme {

    /** Urban city environment — clean state is a clear blue sky. */
    CITY       ("City",          new Color(0.53f, 0.81f, 0.98f, 1f)),

    /** Coastal beach environment — clean state is a warm sandy sky. */
    BEACH      ("Beach",         new Color(0.98f, 0.90f, 0.60f, 1f));
    // ── Fields ────────────────────────────────────────────────────────────────

    /** Display name shown on the map selection screen. */
    private final String displayName;

    /**
     * The background colour that represents a fully clean (0% pollution)
     * version of this environment.
     */
    private final Color cleanColor;

    // ── Constructor ───────────────────────────────────────────────────────────

    MapTheme(String displayName, Color cleanColor) {
        this.displayName = displayName;
        this.cleanColor  = cleanColor;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Returns the display name for this map theme. */
    public String getDisplayName() { return displayName; }

    /**
     * Returns the background colour representing a fully clean environment
     * for this theme. Used by {@link BackgroundManager} as the interpolation
     * target when pollution approaches zero.
     */
    public Color getCleanColor() { return cleanColor; }
}
