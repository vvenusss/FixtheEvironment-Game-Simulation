package io.github.some_example_name.lwjgl3;

import java.util.List;

/**
 * BorderEnforcer — Keeps border-bound entities within the screen boundaries.
 *
 * <h3>Raindrop integration change</h3>
 * {@link #enforce(Entity)} now checks {@link Entity#isBorderBound()} before
 * applying any position correction or velocity flip. Entities with
 * {@code borderBound = false} (e.g., {@link Raindrop}) are silently skipped,
 * allowing them to fall off the bottom of the screen and be culled by
 * {@link RaindropSpawner}.
 *
 * All other behaviour is unchanged.
 */
public class BorderEnforcer {

    private float minX, maxX, minY, maxY;

    public BorderEnforcer(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void enforceAll(List<Entity> entities) {
        if (entities == null) return;
        for (Entity e : entities) enforce(e);
    }

    /**
     * Enforces screen borders for a single entity.
     *
     * Skips the entity entirely if {@link Entity#isBorderBound()} returns
     * {@code false}, allowing non-border-bound entities (e.g., raindrops) to
     * pass through the screen edges without being bounced.
     */
    public void enforce(Entity entity) {
        if (entity == null) return;

        // Skip entities that are not subject to border enforcement (e.g., Raindrop).
        if (!entity.isBorderBound()) return;

        float x = entity.getX(), y = entity.getY();
        float w = entity.getWidth(), h = entity.getHeight();

        // ── X axis ────────────────────────────────────────────────────────────
        if (x < minX) {
            entity.setX(minX);
            bounceX(entity, true);
        } else if (x + w > maxX) {
            entity.setX(maxX - w);
            bounceX(entity, false);
        }

        // ── Y axis ────────────────────────────────────────────────────────────
        if (y < minY) {
            entity.setY(minY);
            bounceY(entity, true);
        } else if (y + h > maxY) {
            entity.setY(maxY - h);
            bounceY(entity, false);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void bounceX(Entity entity, boolean positiveDirection) {
        if (entity instanceof MobileEntity) {
            MobileEntity m = (MobileEntity) entity;
            m.setVelocityX(positiveDirection
                ? Math.abs(m.getVelocityX())
                : -Math.abs(m.getVelocityX()));
        }
    }

    private void bounceY(Entity entity, boolean positiveDirection) {
        if (entity instanceof MobileEntity) {
            MobileEntity m = (MobileEntity) entity;
            m.setVelocityY(positiveDirection
                ? Math.abs(m.getVelocityY())
                : -Math.abs(m.getVelocityY()));
        }
    }

    // ── Bound setters ─────────────────────────────────────────────────────────

    public void setMaxX(float maxX) { this.maxX = maxX; }
    public void setMaxY(float maxY) { this.maxY = maxY; }
}
