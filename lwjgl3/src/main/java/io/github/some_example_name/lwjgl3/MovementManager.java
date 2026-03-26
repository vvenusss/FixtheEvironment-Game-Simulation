package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;

/**
 * MovementManager — Owns movement direction computation and entity velocity.
 *
 * Single Responsibility (SRP): MovementManager has ONE responsibility:
 * translate input state into entity velocity and drive movement controllers
 * each frame. It is the correct owner of getMovementDirection() because
 * movement is a movement concern, not an input-device concern. IoManager only
 * provides raw key queries.
 *
 * Open/Closed (OCP): New player entities can be registered without modifying
 * this class.
 *
 * Dependency Inversion (DIP): Depends on {@link IInputProvider} via constructor
 * injection; never calls Gdx.input directly and never references the concrete
 * {@link IoManager} class.
 *
 * Player speed is passed in at registration time (not hardcoded here), so the
 * screen — which knows the game design — decides how fast the player moves.
 */
public class MovementManager {

	private final IInputProvider inputProvider;

	/** Player-controlled entities and their associated speeds. */
	private final List<MobileEntity> playerEntities = new ArrayList<>();
	private final List<Float> playerSpeeds = new ArrayList<>();

	public MovementManager(IInputProvider inputProvider) {
		this.inputProvider = inputProvider;
	}

	// ── Registration ──────────────────────────────────────────────────────────

	/**
	 * Register a player-controlled entity with an explicit speed (pixels/sec).
	 * Speed is supplied by the caller (screen) so MovementManager stays generic.
	 */
	public void registerPlayerEntity(MobileEntity entity, float speed) {
		if (entity != null && !playerEntities.contains(entity)) {
			playerEntities.add(entity);
			playerSpeeds.add(speed);
		}
	}

	/** Convenience overload using a default speed of 200 px/s. */
	public void registerPlayerEntity(MobileEntity entity) {
		registerPlayerEntity(entity, 200f);
	}

	public void unregisterPlayerEntity(MobileEntity entity) {
		int idx = playerEntities.indexOf(entity);
		if (idx >= 0) {
			playerEntities.remove(idx);
			playerSpeeds.remove(idx);
		}
	}

	// ── Movement direction ──────────────────────────────────────────────────────

	/**
	 * Computes a normalised direction Vector2 from WASD / arrow keys.
	 *
	 * This belongs in MovementManager (not IoManager) because computing a movement
	 * direction is a movement concern. IoManager only wraps the raw device; it does
	 * not interpret what the keys mean for game movement.
	 */
	public Vector2 getMovementDirection() {
		float x = 0;
		float y = 0;

		if (inputProvider.isKeyPressed(Input.Keys.W) || inputProvider.isKeyPressed(Input.Keys.UP))
			y = 1;
		if (inputProvider.isKeyPressed(Input.Keys.S) || inputProvider.isKeyPressed(Input.Keys.DOWN))
			y = -1;
		if (inputProvider.isKeyPressed(Input.Keys.A) || inputProvider.isKeyPressed(Input.Keys.LEFT))
			x = -1;
		if (inputProvider.isKeyPressed(Input.Keys.D) || inputProvider.isKeyPressed(Input.Keys.RIGHT))
			x = 1;

		// Normalise diagonal movement so speed is consistent.
		if (x != 0 && y != 0) {
			float len = (float) Math.sqrt(x * x + y * y);
			x /= len;
			y /= len;
		}

		return new Vector2(x, y);
	}

	/** True while any movement key is held. */
	public boolean isMoving() {
		return inputProvider.isKeyPressed(Input.Keys.W) || inputProvider.isKeyPressed(Input.Keys.S)
				|| inputProvider.isKeyPressed(Input.Keys.A) || inputProvider.isKeyPressed(Input.Keys.D)
				|| inputProvider.isKeyPressed(Input.Keys.UP) || inputProvider.isKeyPressed(Input.Keys.DOWN)
				|| inputProvider.isKeyPressed(Input.Keys.LEFT) || inputProvider.isKeyPressed(Input.Keys.RIGHT);
	}

	// ── Update ────────────────────────────────────────────────────────────────

	/**
	 * Called every frame by {@link GameMaster} (only when not paused).
	 * Computes movement direction and applies speed-scaled velocity to all
	 * registered player-controlled entities.
	 *
	 * @param deltaTime Seconds since the last frame.
	 */
	public void update(float deltaTime) {
		Vector2 direction = getMovementDirection();
		for (int i = 0; i < playerEntities.size(); i++) {
			MobileEntity entity = playerEntities.get(i);
			float speed = playerSpeeds.get(i);
			if (entity != null) {
				entity.setVelocity(direction.x * speed, direction.y * speed);
			}
		}
	}

	/** Clears all registrations (e.g., on screen unload). */
	public void clear() {
		playerEntities.clear();
		playerSpeeds.clear();
	}

	// ── Accessors ─────────────────────────────────────────────────────────────

	/** Returns the underlying input provider (typed as the interface). */
	public IInputProvider getInputProvider() {
		return inputProvider;
	}
}
