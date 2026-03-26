package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionHandling — Resolves collisions using a pluggable strategy.
 *
 * <p>Strategy Pattern: Delegates to {@link CollisionResponseStrategy#resolve(Collision)}
 * for each detected collision. New response types can be added without modifying
 * this class (Open/Closed Principle).
 *
 * <p>Dependency Inversion: Depends on {@link CollisionResponseStrategy} interface,
 * not concrete implementations.
 */
public class CollisionHandling {

    private final String                     collisionResponseType;
    private final CollisionResponseStrategy  strategy;
    private final List<Collision>            resolvedCollisions;


    public CollisionHandling(String collisionResponseType,
                             CollisionResponseStrategy strategy) {
        this.collisionResponseType = collisionResponseType;
        this.strategy              = strategy;
        this.resolvedCollisions    = new ArrayList<>();
    }


    public void resolveCollision(Collision collision) {
        if (collision == null) return;
        strategy.resolve(collision);
        resolvedCollisions.add(collision);
    }

    public void clearResolvedCollisions() {
        resolvedCollisions.clear();
    }

    public String getCollisionResponseType() { return collisionResponseType; }
}
