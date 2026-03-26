package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CollisionDetection — Detects overlapping entities using AABB.
 *
 * <p>Single Responsibility: Only detects collisions; does not resolve them.
 *
 * <p>When {@code thresholdDetection > 0}, bounds are expanded before the overlap
 * check, making collisions slightly more forgiving. With {@code 0}, exact
 * overlap is required.
 */
public class CollisionDetection {

    private final float          thresholdDetection;
    private final List<Collision> activeCollisions;

    public CollisionDetection(float thresholdDetection) {
        this.thresholdDetection = thresholdDetection;
        this.activeCollisions   = new ArrayList<>();
    }

    /**
     * Returns true if the two entities are colliding (overlapping).
     * Uses threshold expansion when thresholdDetection > 0.
     */
    public boolean collisionDetection(Entity a, Entity b) {
        if (a == null || b == null || a == b) return false;

        if (thresholdDetection <= 0f) {
            return a.collidesWith(b);
        }

        float t = thresholdDetection / 2f;
        Rectangle expandedA = new Rectangle(
            a.getX() - t, a.getY() - t,
            a.getWidth() + thresholdDetection, a.getHeight() + thresholdDetection);
        Rectangle expandedB = new Rectangle(
            b.getX() - t, b.getY() - t,
            b.getWidth() + thresholdDetection, b.getHeight() + thresholdDetection);
        return expandedA.overlaps(expandedB);
    }


    public void checkAllCollisions(List<Entity> entities) {
        activeCollisions.clear();
        if (entities == null) return;

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);
                if (collisionDetection(a, b)) {
                    activeCollisions.add(new Collision(a, b));
                }
            }
        }
    }

    /** Returns an unmodifiable view of active collisions (encapsulation). */
    public List<Collision> getActiveCollisions() {
        return Collections.unmodifiableList(activeCollisions);
    }

    public void clearCollisions() { activeCollisions.clear(); }
}
