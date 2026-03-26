package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * PollutionObject — A falling pollution source (factory, car, or trash pile).
 *
 * <h3>Game effects (applied by {@link FallingObjectSpawner})</h3>
 * <ul>
 *   <li><b>Collected by bucket:</b> pollution increases, city health decreases,
 *       score decreases.</li>
 *   <li><b>Reaches the ground:</b> pollution increases, city health decreases.</li>
 * </ul>
 *
 * <h3>Visual representation</h3>
 * Drawn as a dark-coloured rectangle. Colour varies by type:
 * <ul>
 *   <li>FACTORY — dark grey (smoke)</li>
 *   <li>CAR — dark brown (exhaust)</li>
 *   <li>TRASH — dark olive (waste)</li>
 * </ul>
 * When textures are provided, override {@link #drawSprite} in this class.
 */
public class PollutionObject extends FallingObject {

    // ── Colours per type ──────────────────────────────────────────────────────
    private static final Color COLOR_FACTORY = new Color(0.30f, 0.30f, 0.30f, 1f); // dark grey
    private static final Color COLOR_CAR     = new Color(0.45f, 0.25f, 0.10f, 1f); // dark brown
    private static final Color COLOR_TRASH   = new Color(0.35f, 0.40f, 0.10f, 1f); // dark olive

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a pollution object.
     *
     * @param x         Horizontal position in pixels.
     * @param y         Vertical spawn position in pixels (above screen).
     * @param size      Bounding box size in pixels.
     * @param fallSpeed Fall speed in pixels per second.
     * @param type      Must be one of {@link ObjectType#FACTORY},
     *                  {@link ObjectType#CAR}, or {@link ObjectType#TRASH}.
     * @throws IllegalArgumentException if type is not a pollution type.
     */
    public PollutionObject(float x, float y, float size, float fallSpeed, ObjectType type, String texturePath) {
        super(x, y, size, fallSpeed, type, texturePath);
        if (!type.isPollution()) {
            throw new IllegalArgumentException(
                "PollutionObject requires a pollution ObjectType, got: " + type);
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected Color getShapeColor() {
        switch (getType()) {
            case FACTORY: return COLOR_FACTORY;
            case CAR:     return COLOR_CAR;
            case TRASH:   return COLOR_TRASH;
            default:      return Color.DARK_GRAY;
        }
    }

    @Override
    protected void drawFallingShape(ShapeRenderer sr) {
        // Draw as a filled rectangle with a slightly darker border effect
        // by drawing a slightly larger dark rect first.
        sr.setColor(Color.BLACK);
        sr.rect(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2);
        sr.setColor(getShapeColor());
        sr.rect(getX(), getY(), getWidth(), getHeight());

        // Draw a small "X" marker to distinguish pollution from green solutions.
        // This is achieved by drawing two thin diagonal rectangles.
        float cx = getX() + getWidth()  / 2f;
        float cy = getY() + getHeight() / 2f;
        float arm = getWidth() * 0.25f;
        sr.setColor(Color.RED);
        sr.rectLine(cx - arm, cy - arm, cx + arm, cy + arm, 2f);
        sr.rectLine(cx + arm, cy - arm, cx - arm, cy + arm, 2f);
    }
}