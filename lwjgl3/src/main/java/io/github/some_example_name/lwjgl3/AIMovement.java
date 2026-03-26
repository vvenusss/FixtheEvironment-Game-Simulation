package io.github.some_example_name.lwjgl3;

/**
 * AIMovement — Autonomous movement controller that steers an entity toward a target.
 *
 * <h3>Chasing logic</h3>
 * Every frame, the direction vector from the entity's centre to the target's
 * centre is computed, normalised, and applied as velocity. This means the AI
 * always re-aims after any bounce or deflection — no special post-bounce
 * callback is needed for direction recalculation.
 *
 * <h3>AI-gets-stuck fix</h3>
 * The previous version had a conflict: {@link BorderEnforcer} flips the
 * entity's velocity on a border hit, but on the very next frame AIMovement
 * would immediately overwrite that velocity with a fresh direction toward the
 * player — which could point straight back into the same border, causing the
 * entity to vibrate against the wall.
 *
 * The fix is a <b>post-bounce cooldown</b>. When the entity's velocity is
 * detected to have been flipped (i.e., the direction changed significantly
 * from the previous frame), a short cooldown timer is started. During the
 * cooldown, AIMovement does NOT overwrite the velocity — it lets the
 * BorderEnforcer's bounce carry the entity away from the wall first. Once the
 * cooldown expires, normal chasing resumes.
 *
 * <h3>Single Responsibility (SRP)</h3>
 * AIMovement only decides velocity. It does not read input, handle collisions,
 * or manage entity lifecycle. Those concerns belong to {@link MovementManager},
 * {@link CollisionManager}, and {@link EntityManager} respectively.
 */
public class AIMovement {

    private final MobileEntity entity;
    private final MobileEntity target;
    private float speed;

    /**
     * Cooldown timer in seconds. While > 0, AIMovement does not overwrite
     * velocity, allowing the BorderEnforcer's bounce to carry the entity away
     * from the wall before chasing resumes.
     */
    private float bounceCooldown = 0f;

    /**
     * Duration of the post-bounce cooldown in seconds.
     * 0.15 s is enough for the entity to travel ~1 entity-width away from
     * the border at typical AI speeds (150–200 px/s).
     */
    private static final float BOUNCE_COOLDOWN_SECONDS = 0.15f;

    /**
     * Dot-product threshold used to detect a velocity reversal caused by a
     * border bounce. If the dot product of the previous and current velocity
     * directions is negative, the velocity was flipped (bounce occurred).
     */
    private static final float BOUNCE_DETECTION_THRESHOLD = -0.5f;

    /** Velocity direction from the previous frame (used for bounce detection). */
    private float prevVx = 0f;
    private float prevVy = 0f;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param entity The AI-controlled entity.
     * @param target The entity to chase (typically the player).
     * @param speed  Movement speed in pixels per second.
     */
    public AIMovement(MobileEntity entity, MobileEntity target, float speed) {
        this.entity = entity;
        this.target = target;
        this.speed  = speed;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Called every frame by {@link MovementManager#update(float)}.
     *
     * <ol>
     *   <li>Detect if a border bounce occurred this frame (velocity was flipped
     *       by BorderEnforcer since the last AIMovement update).</li>
     *   <li>If a bounce was detected, start the cooldown and skip velocity
     *       recalculation this frame.</li>
     *   <li>If the cooldown is active, count it down and skip recalculation.</li>
     *   <li>Otherwise, compute the normalised direction to the target and apply
     *       it as velocity.</li>
     * </ol>
     *
     * @param deltaTime Time elapsed since the last frame, in seconds.
     */
    public void update(float deltaTime) {
        if (target == null || entity == null) return;

        float currentVx = entity.getVelocityX();
        float currentVy = entity.getVelocityY();

        // ── Bounce detection ──────────────────────────────────────────────────
        // If the velocity direction reversed significantly since last frame,
        // BorderEnforcer must have bounced the entity. Start cooldown.
        if (prevVx != 0 || prevVy != 0) {
            float prevLen    = (float) Math.sqrt(prevVx * prevVx + prevVy * prevVy);
            float currentLen = (float) Math.sqrt(currentVx * currentVx + currentVy * currentVy);

            if (prevLen > 0.01f && currentLen > 0.01f) {
                // Normalised dot product of previous and current velocity directions.
                float dot = (prevVx / prevLen) * (currentVx / currentLen)
                          + (prevVy / prevLen) * (currentVy / currentLen);

                if (dot < BOUNCE_DETECTION_THRESHOLD) {
                    // Velocity was reversed — a bounce occurred.
                    bounceCooldown = BOUNCE_COOLDOWN_SECONDS;
                }
            }
        }

        // ── Cooldown ──────────────────────────────────────────────────────────
        if (bounceCooldown > 0f) {
            bounceCooldown -= deltaTime;
            // Record current velocity for next frame's bounce detection.
            prevVx = entity.getVelocityX();
            prevVy = entity.getVelocityY();
            return; // Do not overwrite velocity during cooldown.
        }

        // ── Chase logic ───────────────────────────────────────────────────────
        // Compute vector from entity centre to target centre.
        float ex = entity.getX() + entity.getWidth()  / 2f;
        float ey = entity.getY() + entity.getHeight() / 2f;
        float tx = target.getX() + target.getWidth()  / 2f;
        float ty = target.getY() + target.getHeight() / 2f;

        float dx  = tx - ex;
        float dy  = ty - ey;
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        if (len > 0.01f) {
            float nx = dx / len;
            float ny = dy / len;
            entity.setVelocity(nx * speed, ny * speed);
        }

        // Record velocity for next frame's bounce detection.
        prevVx = entity.getVelocityX();
        prevVy = entity.getVelocityY();
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public MobileEntity getEntity()           { return entity; }
    public MobileEntity getTarget()           { return target; }
    public float        getSpeed()            { return speed; }
    public void         setSpeed(float speed) { this.speed = speed; }
}
