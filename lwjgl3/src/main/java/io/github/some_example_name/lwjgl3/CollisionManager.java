package io.github.some_example_name.lwjgl3;

import java.util.List;

/**
 * CollisionManager — Coordinates collision detection and resolution.
 *
 * Single Responsibility (SRP):
 *   - Composes CollisionDetection and CollisionHandling.
 *   - Runs the detect → resolve pipeline each frame via {@link #update(List)}.
 *   - Provides isColliding() for point-in-time checks by screens.
 *   - Border enforcement is delegated to BorderEnforcer (SRP: that concern lives there).
 *   - Window resize handling is implemented via resize() (declared on ICollisionManager).
 *
 * Note: {@link #update(List)} is invoked by callers who need centralized
 * entity-to-entity collision detection and resolution. FallingObjectSpawner
 * handles bucket+falling-object collisions internally for its specific logic.
 *
 * Dependency Inversion (DIP):
 *   Implements ICollisionManager so GameMaster depends on the abstraction.
 *   GameMaster calls resize() through the interface — no instanceof or downcast needed.
 */
public class CollisionManager implements ICollisionManager {

    private float minX, maxX, minY, maxY;

    private final CollisionDetection collisionDetection;
    private final CollisionHandling  collisionHandling;
    private final BorderEnforcer     borderEnforcer;

    /**
     * @param strategy Injected collision response strategy (DIP: depend on
     *                 abstraction). Caller provides concrete implementation
     *                 (e.g. {@link BounceCollisionStrategy}).
     */
    public CollisionManager(float minX, float maxX,
                            float minY, float maxY,
                            float thresholdDetection,
                            String responseType,
                            CollisionResponseStrategy strategy) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;

        this.collisionDetection = new CollisionDetection(thresholdDetection);
        this.collisionHandling  = new CollisionHandling(responseType, strategy);
        this.borderEnforcer     = new BorderEnforcer(minX, maxX, minY, maxY);
    }

    // ── ICollisionManager ─────────────────────────────────────────────────────

    /**
     * Detect and resolve all entity-to-entity collisions for the given list.
     * Call this each frame when centralized collision handling is needed.
     */
    @Override
    public void update(List<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;

        collisionDetection.checkAllCollisions(entities);

        for (Collision collision : collisionDetection.getActiveCollisions()) {
            collisionHandling.resolveCollision(collision);
        }

        clearCollisions();
    }

    @Override
    public void clearCollisions() {
        collisionDetection.clearCollisions();
        collisionHandling.clearResolvedCollisions();
    }

    /**
     * Point-in-time AABB overlap check between two entities.
     * Delegates to {@link Entity#collidesWith(Entity)} for consistency.
     */
    @Override
    public boolean isColliding(Entity a, Entity b) {
        if (a == null || b == null) return false;
        return a.collidesWith(b);
    }

    /**
     * Enforce world borders on all entities.
     * Delegates to BorderEnforcer (SRP: border logic lives there, not here).
     */
    @Override
    public void enforceBorders(List<Entity> entities, float width, float height) {
        borderEnforcer.setMaxX(width);
        borderEnforcer.setMaxY(height);
        borderEnforcer.enforceAll(entities);
    }

    /**
     * Update the collision bounds when the window is resized.
     * Called by GameMaster through the ICollisionManager interface (DIP fix:
     * no instanceof check or downcast in GameMaster required).
     */
    @Override
    public void resize(float width, float height) {
        this.maxX = width;
        this.maxY = height;
        borderEnforcer.setMaxX(width);
        borderEnforcer.setMaxY(height);
    }

    // ── Bound accessors ───────────────────────────────────────────────────────

    public float getMinX() { return minX; }
    public float getMaxX() { return maxX; }
    public float getMinY() { return minY; }
    public float getMaxY() { return maxY; }
}
