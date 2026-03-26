package io.github.some_example_name.lwjgl3;

/**
 * CollisionResponseStrategy — Strategy for resolving a detected collision.
 *
 * <p>Strategy Pattern: Implementations define what happens when two entities
 * overlap (e.g. bounce, damage, remove). {@link CollisionHandling} delegates
 * to the injected strategy without knowing the concrete type.
 *
 * <p>Liskov Substitution: Any implementation can replace another; callers
 * depend only on this contract.
 *
 * @see BounceCollisionStrategy
 */
public interface CollisionResponseStrategy {

    void resolve(Collision collision);
}
