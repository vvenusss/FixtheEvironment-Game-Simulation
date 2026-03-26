package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * EntityManager — The single authority for all entity lifecycle management
 * within a screen.
 *
 * <h3>Why this class exists (SRP)</h3>
 * Without EntityManager, every screen would maintain its own {@code List<Entity>},
 * leading to duplicated add/remove/update/render logic across StartScreen,
 * ActivityScreen, and EndScreen. EntityManager centralises all of that so
 * screens only need to call {@code addEntity()} and {@code removeEntity()}.
 *
 * <h3>Pending-queue pattern</h3>
 * Entities are never added or removed directly from the live {@code entities}
 * list mid-frame. Instead they are queued in {@code pendingAdd} and
 * {@code pendingRemove} and flushed at safe points in {@code update()}.
 * This prevents {@link java.util.ConcurrentModificationException} that would
 * occur if, for example, a collision handler removed an entity while the
 * update loop was still iterating over the list.
 *
 * <h3>Read-only access</h3>
 * {@link #getEntitiesReadOnly()} returns an unmodifiable view so that
 * CollisionManager and other utilities can iterate the list safely without
 * being able to accidentally modify it.
 *
 * <h3>Ownership</h3>
 * Each {@link Screen} owns exactly one EntityManager (instantiated in the
 * Screen constructor). GameMaster does NOT own or create EntityManagers —
 * that is the screen's responsibility.
 */
public class EntityManager {

    // ── Entity lists ──────────────────────────────────────────────────────────

    /**
     * The live list of entities that are currently active in the game world.
     * Only modified via flushAdds() and flushRemoves() to avoid
     * ConcurrentModificationException during iteration.
     */
    private final List<Entity> entities = new ArrayList<>();

    /**
     * Entities queued for addition at the start of the next update cycle.
     * Using a queue means addEntity() is safe to call from anywhere, including
     * from within an entity's own update() method.
     */
    private final List<Entity> pendingAdd = new ArrayList<>();

    /**
     * Entities queued for removal after the current update cycle completes.
     * Deferred removal ensures the update loop finishes iterating before the
     * entity disappears from the list.
     */
    private final List<Entity> pendingRemove = new ArrayList<>();

    // ── Camera ────────────────────────────────────────────────────────────────

    /**
     * Orthographic camera used to project entity positions into screen space.
     * Owned here so all entities are rendered with the same projection matrix,
     * regardless of which screen is active.
     */
    private final OrthographicCamera camera;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a new EntityManager and initialises the camera to the current
     * window dimensions. Called by {@link Screen} — not by GameMaster.
     */
    public EntityManager() {
        // Camera is set to ortho mode so (0,0) is bottom-left of the screen,
        // matching LibGDX's default world coordinate system.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Queue an entity for addition at the start of the next update.
     *
     * The entity is NOT added immediately; it enters {@code pendingAdd} and
     * is flushed into the live list at the beginning of the next call to
     * {@link #update(float)}. This is safe to call at any time, including
     * from within an entity's own {@code update()} method.
     *
     * @param entity The entity to add. Null values are silently ignored.
     */
    public void addEntity(Entity entity) {
        if (entity != null) pendingAdd.add(entity);
    }

    /**
     * Queue an entity for removal after the current update loop completes.
     *
     * The entity remains in the live list until the end of the current
     * {@link #update(float)} call, so it will still receive its update this
     * frame. It is removed cleanly in {@code flushRemoves()} afterwards.
     *
     * @param entity The entity to remove.
     * @return The same entity, so callers can chain or inspect it.
     */
    public Entity removeEntity(Entity entity) {
        if (entity == null) return null;
        pendingRemove.add(entity);
        return entity;
    }

    /**
     * Update all active entities for one frame.
     *
     * Execution order:
     * <ol>
     *   <li>{@code flushAdds()}   — move pending additions into the live list.</li>
     *   <li>Iterate and call {@code entity.update(deltaTime)} on each entity.</li>
     *   <li>{@code flushRemoves()} — remove any entities queued for deletion.</li>
     * </ol>
     *
     * The flush-before-update order ensures newly added entities are updated
     * in the same frame they are added. The flush-after-update order ensures
     * removed entities still complete their last update before disappearing.
     *
     * @param deltaTime Time elapsed since the last frame, in seconds.
     */
    public void update(float deltaTime) {
        flushAdds();                          // Step 1: add queued entities
        for (Entity e : entities) {
            e.update(deltaTime);              // Step 2: update each entity
        }
        flushRemoves();                       // Step 3: remove queued entities
    }

    /**
     * Render all active entities using the provided renderers.
     *
     * The camera projection matrix is applied to both renderers so all entities
     * are drawn in world space. SpriteBatch handles texture-based sprites;
     * ShapeRenderer handles primitive shapes (rectangles, circles, etc.).
     * Either renderer may be null if not needed for a given screen.
     *
     * @param spriteBatch   Renderer for textured sprites. May be null.
     * @param shapeRenderer Renderer for primitive shapes. May be null.
     */
    public void drawAll(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        camera.update();

        // Sprite pass — only runs if a SpriteBatch is provided.
        if (spriteBatch != null) {
            spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatch.begin();
            for (Entity e : entities) e.drawSprite(spriteBatch);
            spriteBatch.end();
        }

        // Shape pass — only runs if a ShapeRenderer is provided.
        if (shapeRenderer != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Entity e : entities) e.drawShape(shapeRenderer);
            shapeRenderer.end();
        }
    }

    /**
     * Returns an unmodifiable view of the active entity list.
     *
     * External systems (CollisionManager, BorderEnforcer) receive this view
     * so they can iterate entities without being able to modify the list.
     * Any attempt to call add() or remove() on the returned list will throw
     * {@link UnsupportedOperationException}.
     *
     * @return An unmodifiable live view of the entity list.
     */
    public List<Entity> getEntitiesReadOnly() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Remove all entities immediately and clear all pending queues.
     *
     * Called by {@link Screen#onUnload()} when a screen is being torn down.
     * After this call the EntityManager is empty and ready to be garbage-collected
     * along with the screen that owns it.
     */
    public void clear() {
        entities.clear();
        pendingAdd.clear();
        pendingRemove.clear();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Move all entities from the pending-add queue into the live list.
     * Called at the start of update() before any entity is updated.
     */
    private void flushAdds() {
        if (!pendingAdd.isEmpty()) {
            entities.addAll(pendingAdd);
            pendingAdd.clear();
        }
    }

    /**
     * Remove all entities in the pending-remove queue from the live list.
     * Called at the end of update() after all entities have been updated.
     */
    private void flushRemoves() {
        if (!pendingRemove.isEmpty()) {
            entities.removeAll(pendingRemove);
            pendingRemove.clear();
        }
    }
}
