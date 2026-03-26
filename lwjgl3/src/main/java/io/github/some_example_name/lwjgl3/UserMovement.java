package io.github.some_example_name.lwjgl3;

/**
 * UserMovement — Event-based movement for a user-controlled entity.
 *
 * Responsibilities (SRP / DIP):
 *   - Implements IMovable so it can be used wherever movable objects are expected.
 *   - Reads direction from an injected InputHandler (not from Gdx.input directly).
 *   - Applies speed-scaled velocity to its position each frame.
 *
 * InputHandler internally delegates to Keyboard (direction polling) and
 * Mouse (position polling) — no raw Gdx.input calls anywhere in this pipeline.
 *
 * Registered with MovementManager via registerUserMovement().
 */
public class UserMovement implements IMovable {

    private Vector2 position;
    private Vector2 velocity;
    private float   speed;

    private final InputHandler inputHandler;

    public UserMovement(Vector2 startPosition, float speed, InputHandler inputHandler) {
        this.position     = startPosition;
        this.velocity     = new Vector2(0, 0);
        this.speed        = speed;
        this.inputHandler = inputHandler;
    }

    // ── IMovable ──────────────────────────────────────────────────────────────

    @Override
    public void updatePosition(float dt) {
        // Refresh input devices, then obtain direction from InputHandler.
        inputHandler.update();
        Vector2 direction = inputHandler.getAxis();
        velocity.set(direction.x, direction.y);

        // Apply speed-scaled movement.
        Vector2 movement = velocity.multiply(speed * dt);
        position.add(movement);
    }

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public void setPosition(Vector2 p) { this.position = p; }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public float getSpeed()            { return speed; }
    public void  setSpeed(float speed) { this.speed = speed; }
}
