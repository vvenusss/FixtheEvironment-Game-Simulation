package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * MobileEntity — A concrete {@link Entity} subclass that moves under velocity.
 *
 * <h3>Role in the entity hierarchy</h3>
 * {@link Entity} is the abstract base that defines position, dimensions, and
 * rendering hooks. MobileEntity extends it with a velocity vector ({@code vx},
 * {@code vy}) and applies that velocity to position each frame in
 * {@link #update(float)}.
 *
 * <h3>Why there is one MobileEntity class, not three (feedback point 25)</h3>
 * The UML diagram previously showed "mobile 1", "mobile 2", "mobile 3" as
 * separate subclasses under the abstract Entity. Those were placeholder nodes
 * used during design to represent the three moving objects in the game (the
 * player, enemy 1, and enemy 2). In the final implementation they are all
 * instances of this single {@code MobileEntity} class — differentiated at
 * runtime by their position, colour, and the movement controller registered
 * for them in {@link MovementManager} (player uses direction input; enemies
 * use {@link AIMovement}). Separate subclasses would violate DRY and add no
 * new attributes or behaviour.
 *
 * <h3>Single Responsibility (SRP)</h3>
 * MobileEntity is responsible only for storing and applying velocity. It does
 * not read input, make AI decisions, or handle collisions. Those concerns
 * belong to {@link MovementManager}, {@link AIMovement}, and
 * {@link CollisionManager} respectively.
 *
 * <h3>Attributes</h3>
 * <ul>
 *   <li>{@code vx} — horizontal velocity in pixels per second (positive = right)</li>
 *   <li>{@code vy} — vertical velocity in pixels per second (positive = up)</li>
 *   <li>{@code color} — render colour, set by the screen at creation time</li>
 * </ul>
 */
public class MobileEntity extends Entity {

    /** Horizontal velocity in pixels per second. Positive = moving right. */
    private float vx;

    /** Vertical velocity in pixels per second. Positive = moving up. */
    private float vy;

    /**
     * Render colour for the entity's shape.
     * Defaults to white; set by the owning screen (e.g., GREEN for player,
     * RED / ORANGE for enemies) to distinguish entities visually.
     */
    private Color color = Color.WHITE;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Create a new MobileEntity with an initial position, size, and velocity.
     *
     * @param x  Initial X position (bottom-left corner), in pixels.
     * @param y  Initial Y position (bottom-left corner), in pixels.
     * @param w  Width of the entity, in pixels.
     * @param h  Height of the entity, in pixels.
     * @param vx Initial horizontal velocity, in pixels per second.
     * @param vy Initial vertical velocity, in pixels per second.
     */
    public MobileEntity(float x, float y, float w, float h, float vx, float vy) {
        super(x, y, w, h);
        this.vx = vx;
        this.vy = vy;
    }

    // ── Entity lifecycle ──────────────────────────────────────────────────────

    /**
     * Advance the entity's position by its current velocity.
     *
     * Called every frame by {@link EntityManager#update(float)}.
     * Velocity is set externally by {@link MovementManager} (for the player)
     * or {@link AIMovement} (for enemies); this method only applies it.
     *
     * @param deltaTime Time elapsed since the last frame, in seconds.
     *                  Multiplying by deltaTime makes movement frame-rate independent.
     */
    @Override
    public void update(float deltaTime) {
        // Position += velocity × time  →  frame-rate independent movement.
        x += vx * deltaTime;
        y += vy * deltaTime;
    }

    /**
     * Render the entity as a filled rectangle using the assigned colour.
     *
     * Called every frame by {@link EntityManager#drawAll(
     * com.badlogic.gdx.graphics.g2d.SpriteBatch,
     * com.badlogic.gdx.graphics.glutils.ShapeRenderer)}.
     *
     * @param shapeRenderer The LibGDX shape renderer (already in Filled mode).
     */
    @Override
    public void drawShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
    }

    // ── Velocity accessors (used by MovementManager and AIMovement) ───────────

    /**
     * Set both velocity components at once.
     * Called by {@link MovementManager} each frame for player-controlled entities,
     * and by {@link AIMovement#update(float)} for AI-controlled entities.
     *
     * @param vx New horizontal velocity in pixels per second.
     * @param vy New vertical velocity in pixels per second.
     */
    public void setVelocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    /** Set horizontal velocity only. */
    public void setVelocityX(float vx) { this.vx = vx; }

    /** Set vertical velocity only. */
    public void setVelocityY(float vy) { this.vy = vy; }

    /** Returns the current horizontal velocity in pixels per second. */
    public float getVelocityX() { return vx; }

    /** Returns the current vertical velocity in pixels per second. */
    public float getVelocityY() { return vy; }

    /**
     * Scale the current velocity vector to the given speed while preserving
     * direction. Has no effect if the entity is stationary (velocity is zero).
     *
     * @param speed The desired speed in pixels per second.
     */
    public void setSpeed(float speed) {
        float length = (float) Math.sqrt(vx * vx + vy * vy);
        if (length > 0) {
            vx = (vx / length) * speed;
            vy = (vy / length) * speed;
        }
    }

    // ── Colour accessors ──────────────────────────────────────────────────────

    /**
     * Set the render colour using a LibGDX {@link Color} object.
     * Typical usage: {@code entity.setColor(Color.RED)}.
     */
    public void setColor(Color color) { this.color = color; }

    /**
     * Set the render colour using RGBA components in the range [0, 1].
     *
     * @param r Red component.
     * @param g Green component.
     * @param b Blue component.
     * @param a Alpha (opacity) component.
     */
    public void setColor(float r, float g, float b, float a) {
        this.color = new Color(r, g, b, a);
    }
}
