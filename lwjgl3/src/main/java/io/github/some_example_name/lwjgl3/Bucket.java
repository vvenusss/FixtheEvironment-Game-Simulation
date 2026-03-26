package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Bucket — The player-controlled collection entity.
 *
 * <h3>Hierarchy</h3>
 * {@code Bucket extends MobileEntity extends Entity}.
 * This is a pure addition to the existing hierarchy — no base classes changed.
 *
 * <h3>Movement</h3>
 * The bucket moves left and right only (fixed Y position near the bottom of
 * the screen). Movement is driven by {@link IoManager} — all input goes
 * through the IO manager, not direct {@code Gdx.input} calls, in compliance
 * with the project's input centralisation policy.
 *
 * <h3>Rendering</h3>
 * Currently drawn as a coloured rectangle (bucket outline). When a texture
 * is provided, override {@link #drawSprite(SpriteBatch)} to render it.
 * The texture slot is already wired — set {@code textureReady = true} and
 * implement the texture draw to switch from shape to texture rendering.
 *
 * <h3>Border enforcement</h3>
 * {@code borderBound = true} (default) — the bucket is kept within the
 * screen by {@link BorderEnforcer}. Only horizontal movement is applied;
 * the Y position is fixed at construction time.
 *
 * <h3>SOLID</h3>
 * <ul>
 *   <li><b>SRP:</b> Bucket only handles its own movement and rendering.
 *       Collision detection and game-effect logic live in
 *       {@link FallingObjectSpawner}.</li>
 *   <li><b>DIP:</b> Depends on {@link IInputProvider} — the same abstraction
 *       used by {@link MovementManager}. The concrete {@link IoManager} is
 *       never referenced directly.</li>
 * </ul>
 */
public class Bucket extends MobileEntity {

    // ── Constants ─────────────────────────────────────────────────────────────

    /** Default bucket width in pixels. */
    public static final float DEFAULT_WIDTH  = 80f;

    /** Default bucket height in pixels. */
    public static final float DEFAULT_HEIGHT = 20f;

    /** Default movement speed in pixels per second. */
    private static final float DEFAULT_SPEED = 300f;

    /** Bucket colour — a neutral light blue to distinguish from falling objects. */
    private static final Color BUCKET_COLOR  = new Color(0.60f, 0.80f, 1.00f, 1f);

    /** Outline colour for the bucket rim. */
    private static final Color RIM_COLOR     = new Color(0.20f, 0.50f, 0.80f, 1f);

    // ── State ─────────────────────────────────────────────────────────────────

    /** Reference to the input provider for reading left/right input. */
    private final IInputProvider ioManager;

    /** Movement speed in pixels per second. */
    private float speed;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a bucket at the given position with default dimensions and speed.
     *
     * @param x         Horizontal centre position in pixels.
     * @param y         Fixed vertical position in pixels (near screen bottom).
     * @param ioManager The input provider used for left/right input polling.
     */
    public Bucket(float x, float y, IInputProvider ioManager) {
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPEED, ioManager);
    }

    /**
     * Creates a bucket with custom dimensions and speed.
     *
     * @param x         Horizontal position in pixels.
     * @param y         Fixed vertical position in pixels.
     * @param width     Bucket width in pixels.
     * @param height    Bucket height in pixels.
     * @param speed     Movement speed in pixels per second.
     * @param ioManager The input provider used for left/right input polling.
     */
    public Bucket(float x, float y, float width, float height,
                  float speed, IInputProvider ioManager) {
        super(x, y, width, height, 0f, 0f);
        this.speed     = speed;
        this.ioManager = ioManager;
        // Bucket stays within screen borders.
        setBorderBound(true);
        setColor(BUCKET_COLOR);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Reads left/right input from {@link IInputProvider} and updates horizontal
     * position. Vertical position is fixed — the bucket never moves up or down.
     *
     * Input keys: LEFT arrow / A = move left; RIGHT arrow / D = move right.
     */
    @Override
    public void update(float deltaTime) {
        float dx = 0f;

        if (ioManager.isKeyPressed(Input.Keys.LEFT)  ||
            ioManager.isKeyPressed(Input.Keys.A)) {
            dx -= speed * deltaTime;
        }
        if (ioManager.isKeyPressed(Input.Keys.RIGHT) ||
            ioManager.isKeyPressed(Input.Keys.D)) {
            dx += speed * deltaTime;
        }

        // Only update X — Y is fixed.
        setX(getX() + dx);
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    /**
     * Draws the bucket as a coloured rectangle with a darker rim at the top.
     * Replace this with texture rendering when assets are available.
     */
    @Override
    public void drawShape(ShapeRenderer sr) {
        if (sr == null) return;

        float x = getX(), y = getY(), w = getWidth(), h = getHeight();

        // Main bucket body
        sr.setColor(BUCKET_COLOR);
        sr.rect(x, y, w, h);

        // Rim at the top (slightly darker)
        sr.setColor(RIM_COLOR);
        sr.rect(x, y + h - 4, w, 4);

        // Left and right walls
        sr.rect(x,         y, 4, h);
        sr.rect(x + w - 4, y, 4, h);
    }

    /**
     * Placeholder for texture-based rendering.
     * Implement this method when bucket texture assets are ready.
     */
    @Override
    public void drawSprite(SpriteBatch batch) {
        // TODO: render bucket texture here when provided.
    }
}
