package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Entity — Abstract base class for all game objects.
 *
 * <h3>Border-bound flag</h3>
 * A {@code borderBound} flag has been added (default {@code true}). Entities
 * that should NOT be bounced by {@link BorderEnforcer} (e.g., {@link Raindrop}
 * objects that should disappear off the bottom of the screen instead of
 * bouncing) can call {@link #setBorderBound(boolean) setBorderBound(false)}.
 *
 * {@link BorderEnforcer} checks this flag before applying border enforcement.
 * All existing entities ({@link MobileEntity} subclasses used as player and
 * enemies) default to {@code true} and are unaffected by this change.
 *
 * No other changes were made to this class.
 */
public abstract class Entity {

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    /**
     * Whether this entity should be bounced by {@link BorderEnforcer}.
     * Defaults to {@code true}. Set to {@code false} for entities like
     * {@link Raindrop} that should pass through screen borders.
     */
    private boolean borderBound = true;

    // ── Constructor ───────────────────────────────────────────────────────────

    protected Entity(float x, float y, float width, float height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    // ── Abstract methods ──────────────────────────────────────────────────────

    public abstract void update(float deltaTime);

    // ── Default rendering (no-op, override in subclasses) ─────────────────────

    public void drawSprite(SpriteBatch spriteBatch)       { }
    public void drawShape(ShapeRenderer shapeRenderer)    { }

    // ── Collision helpers ─────────────────────────────────────────────────────

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean collidesWith(Entity other) {
        return other != null && getBounds().overlaps(other.getBounds());
    }

    // ── Border-bound flag ─────────────────────────────────────────────────────

    /**
     * Returns {@code true} if this entity should be bounced by
     * {@link BorderEnforcer} when it reaches a screen edge.
     * Returns {@code false} for entities that should pass through borders
     * (e.g., {@link Raindrop}).
     */
    public boolean isBorderBound() { return borderBound; }

    /**
     * Sets whether this entity should be subject to border enforcement.
     *
     * @param borderBound {@code false} to allow this entity to pass through
     *                    screen borders without bouncing.
     */
    public void setBorderBound(boolean borderBound) { this.borderBound = borderBound; }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public float getX()      { return x; }
    public float getY()      { return y; }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }
    public void  setX(float x) { this.x = x; }
    public void  setY(float y) { this.y = y; }
}
