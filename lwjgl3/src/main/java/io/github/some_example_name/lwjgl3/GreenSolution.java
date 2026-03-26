package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * GreenSolution — A falling environmental solution (tree, solar panel, or recycling bin).
 *
 * <h3>Game effects (applied by {@link FallingObjectSpawner})</h3>
 * <ul>
 *   <li><b>Collected by bucket:</b> pollution decreases, city health increases,
 *       score increases.</li>
 *   <li><b>Reaches the ground:</b> no effect (missed opportunity).</li>
 * </ul>
 *
 * <h3>Visual representation</h3>
 * Drawn as a bright-coloured shape. Colour and shape vary by type:
 * <ul>
 *   <li>TREE — bright green circle (canopy)</li>
 *   <li>SOLAR_PANEL — bright yellow rectangle</li>
 *   <li>RECYCLING_BIN — bright teal rectangle</li>
 * </ul>
 * When textures are provided, override {@link #drawSprite} in this class.
 */
public class GreenSolution extends FallingObject {

    // ── Colours per type ──────────────────────────────────────────────────────
    private static final Color COLOR_TREE          = new Color(0.13f, 0.70f, 0.13f, 1f); // bright green
    private static final Color COLOR_SOLAR_PANEL   = new Color(1.00f, 0.85f, 0.00f, 1f); // bright yellow
    private static final Color COLOR_RECYCLING_BIN = new Color(0.00f, 0.75f, 0.75f, 1f); // teal

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a green solution object.
     *
     * @param x         Horizontal position in pixels.
     * @param y         Vertical spawn position in pixels (above screen).
     * @param size      Bounding box size in pixels.
     * @param fallSpeed Fall speed in pixels per second.
     * @param type      Must be one of {@link ObjectType#TREE},
     *                  {@link ObjectType#SOLAR_PANEL}, or
     *                  {@link ObjectType#RECYCLING_BIN}.
     * @throws IllegalArgumentException if type is not a green solution type.
     */
    public GreenSolution(float x, float y, float size, float fallSpeed, ObjectType type, String texturePath) {
        super(x, y, size, fallSpeed, type, texturePath);
        if (!type.isGreenSolution()) {
            throw new IllegalArgumentException(
                "GreenSolution requires a green ObjectType, got: " + type);
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected Color getShapeColor() {
        switch (getType()) {
            case TREE:          return COLOR_TREE;
            case SOLAR_PANEL:   return COLOR_SOLAR_PANEL;
            case RECYCLING_BIN: return COLOR_RECYCLING_BIN;
            default:            return Color.GREEN;
        }
    }

    @Override
    protected void drawFallingShape(ShapeRenderer sr) {
        float cx = getX() + getWidth()  / 2f;
        float cy = getY() + getHeight() / 2f;

        if (getType() == ObjectType.TREE) {
            // Draw tree as a circle (canopy) above a small brown trunk.
            float radius = getWidth() * 0.45f;
            // Trunk
            sr.setColor(new Color(0.55f, 0.27f, 0.07f, 1f));
            sr.rect(cx - 3, getY(), 6, getHeight() * 0.35f);
            // Canopy
            sr.setColor(COLOR_TREE);
            sr.circle(cx, cy + getHeight() * 0.15f, radius);
        } else {
            // Draw solar panel and recycling bin as filled rectangles
            // with a bright border.
            sr.setColor(Color.WHITE);
            sr.rect(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2);
            sr.setColor(getShapeColor());
            sr.rect(getX(), getY(), getWidth(), getHeight());

            // Draw a small "✓" checkmark using two lines.
            sr.setColor(Color.WHITE);
            float arm = getWidth() * 0.22f;
            sr.rectLine(cx - arm, cy, cx - arm * 0.3f, cy - arm, 2f);
            sr.rectLine(cx - arm * 0.3f, cy - arm, cx + arm, cy + arm * 0.6f, 2f);
        }
    }
}