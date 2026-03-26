package io.github.some_example_name.lwjgl3;

import java.util.List;

/**
 * ICollisionManager — Abstraction for the collision management system.
 *
 * Dependency Inversion (DIP):
 *   High-level classes (GameMaster, Screen) depend on this interface, not on
 *   the concrete CollisionManager. All methods needed by callers are declared
 *   here so no instance of check or downcast is ever required.
 *
 * Open/Closed (OCP):
 *   New collision implementations can be swapped in without changing callers.
 */
public interface ICollisionManager {

    /** Process all entity-to-entity collisions for the given list. */
    void update(List<Entity> entities);

    /** Clear any stored collision state. */
    void clearCollisions();

    /**
     * Point-in-time AABB overlap check between two entities.
     * Used by ActivityScreen to detect the game-over collision.
     */
    boolean isColliding(Entity a, Entity b);

    /**
     * Enforce world borders on all entities in the list.
     *
     * @param entities The list of entities to clamp.
     * @param width    Current world/window width.
     * @param height   Current world/window height.
     */
    void enforceBorders(List<Entity> entities, float width, float height);

    /**
     * Notify the collision system that the window has been resized.
     * Called by GameMaster each frame to keep border bounds in sync.
     * Declared here so GameMaster never needs to downcast to CollisionManager.
     *
     * @param width  New window width.
     * @param height New window height.
     */
    void resize(float width, float height);
}
