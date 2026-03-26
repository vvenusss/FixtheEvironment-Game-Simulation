package io.github.some_example_name.lwjgl3;

/**
 * Collision — Immutable pair of entities that overlap.
 *
 * <p>Encapsulation: Private final fields; constructor validates non-null.
 * Used by CollisionDetection and CollisionHandling.
 */
public class Collision {

    private final Entity entityA;
    private final Entity entityB;

    public Collision(Entity entityA, Entity entityB) {
        if (entityA == null || entityB == null) {
            throw new IllegalArgumentException("Collision entities cannot be null");
        }
        this.entityA = entityA;
        this.entityB = entityB;
    }

    public Entity getEntityA() { return entityA; }
    public Entity getEntityB() { return entityB; }
}
