package io.github.some_example_name.lwjgl3;

/**
 * BounceCollisionStrategy — Resolves bucket/falling-object collisions.
 *
 * <p>Implements {@link CollisionResponseStrategy}; can be substituted for
 * other strategies (Liskov Substitution).
 *
 * <p>New gameplay rule:
 * - Bucket stays fixed.
 * - FallingObject bounces upward when it hits the bucket.
 * - No score / health / pollution change happens here.
 *   Those outcomes are handled later by top/bottom boundary logic in
 *   FallingObjectSpawner.
 *
 * This keeps collision response separate from gameplay outcome logic.
 */
public class BounceCollisionStrategy implements CollisionResponseStrategy {

    private static final float SEPARATION_EPSILON = 0.1f;

    @Override
    public void resolve(Collision collision) {
        if (collision == null) return;

        Entity a = collision.getEntityA();
        Entity b = collision.getEntityB();
        if (a == null || b == null) return;

        // Only resolve the gameplay collision we actually want:
        // Bucket + FallingObject.
        if (a instanceof Bucket && b instanceof FallingObject) {
            bounceFallingObject((Bucket) a, (FallingObject) b);
        } else if (b instanceof Bucket && a instanceof FallingObject) {
            bounceFallingObject((Bucket) b, (FallingObject) a);
        }

        // All other collisions are intentionally ignored by this strategy.
    }

    /**
     * Bounce the falling object upward and move it just above the bucket so
     * the same overlap is not re-triggered next frame.
     */
    private void bounceFallingObject(Bucket bucket, FallingObject object) {
        if (bucket == null || object == null) return;

        // If the object is already travelling upward, do not bounce it again.
        if (object.getVelocityY() >= 0f) return;

        // Place object just above the bucket to break overlap cleanly.
        object.setY(bucket.getY() + bucket.getHeight() + SEPARATION_EPSILON);

        // Reverse vertical movement upward.
        object.setVelocityY(Math.abs(object.getVelocityY()));
    }
}