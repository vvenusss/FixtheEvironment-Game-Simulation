package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Keyboard — Event-based keyboard input device with built-in polling.
 *
 * Extends AbstractInput for event-driven Action bindings (InputTrigger →
 * Action). Also absorbs the former KeyboardInput polling logic so that
 * direction state is available directly from this class — no separate
 * KeyboardInput needed.
 *
 * Direction constants (UP, DOWN, LEFT, RIGHT, NONE) are defined here and used
 * by InputHandler.getAxis() to produce a Vector2 for UserMovement.
 *
 * SRP: owns all keyboard-level input concerns (event dispatch + direction
 * polling). DIP: registered with InputHandler via constructor injection.
 */
public class Keyboard extends AbstractInput {

	// ── Direction constants (formerly in KeyboardInput) ───────────────────────
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int NONE = -1;

	private int currentDirection = NONE;

	public Keyboard() {
		super(InputType.KEYBOARD);
	}

	// ── Polling update (formerly KeyboardInput.update()) ─────────────────────

	/**
	 * Call once per frame to refresh the current direction state. All Gdx.input
	 * polling is performed here so no other class needs to.
	 */
	public void update() {
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
			currentDirection = UP;
		else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
			currentDirection = DOWN;
		else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
			currentDirection = LEFT;
		else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			currentDirection = RIGHT;
		else
			currentDirection = NONE;
	}

	// ── Accessors ─────────────────────────────────────────────────────────────

	public int getDirection() {
		return currentDirection;
	}

	public void setDirection(int direction) {
		this.currentDirection = direction;
	}
}