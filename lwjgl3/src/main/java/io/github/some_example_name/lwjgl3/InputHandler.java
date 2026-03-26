package io.github.some_example_name.lwjgl3;

/**
 * InputHandler — Combines Keyboard and Mouse into a unified input API.
 *
 * Responsibilities (SRP / DIP):
 *   - Receives Keyboard and Mouse via constructor injection (DIP).
 *   - Calls update() on both devices once per frame.
 *   - Provides getAxis() (direction Vector2) and getMousePos() for UserMovement.
 *   - Does NOT read Gdx.input directly; delegates entirely to Keyboard and Mouse.
 *
 * The former KeyboardInput and MouseInput classes have been merged into
 * Keyboard and Mouse respectively. InputHandler now references those directly.
 *
 * Note: Game-level input (pause, volume, player WASD via IoManager) is handled
 * by IoManager. InputHandler is used exclusively by the UserMovement pipeline.
 */
public class InputHandler {

    private final Keyboard keyboard;
    private final Mouse    mouse;

    public InputHandler(Keyboard keyboard, Mouse mouse) {
        this.keyboard = keyboard;
        this.mouse    = mouse;
    }

    /** Refresh internal state; call once per frame before querying. */
    public void update() {
        keyboard.update();
        mouse.update();
    }

    /**
     * Returns a normalised direction Vector2 based on current keyboard state.
     * Used by UserMovement.updatePosition().
     */
    public Vector2 getAxis() {
        switch (keyboard.getDirection()) {
            case Keyboard.UP:    return new Vector2( 0,  1);
            case Keyboard.DOWN:  return new Vector2( 0, -1);
            case Keyboard.LEFT:  return new Vector2(-1,  0);
            case Keyboard.RIGHT: return new Vector2( 1,  0);
            default:             return new Vector2( 0,  0);
        }
    }

    /** Returns the current mouse position in world coordinates. */
    public Vector2 getMousePos() {
        return mouse.getMousePos();
    }

    /** True while any movement key is held. */
    public boolean isMoving() {
        return keyboard.getDirection() != Keyboard.NONE;
    }
}
