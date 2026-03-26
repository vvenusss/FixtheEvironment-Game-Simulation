package io.github.some_example_name.lwjgl3;

/**
 * IInputProvider — Abstraction over the raw input device.
 *
 * <h3>Why this interface exists (DIP)</h3>
 * Without this interface, high-level modules ({@link MovementManager},
 * {@link ActivityScreen}, {@link PauseScreen}, etc.) would depend directly on
 * the concrete {@link IoManager} class. The Dependency Inversion Principle
 * states that high-level modules should depend on abstractions, not on
 * concrete low-level modules.
 *
 * By depending on {@code IInputProvider}, all consumers are decoupled from
 * the specific input implementation. A test stub, a replay system, or an
 * alternative input backend can be substituted without changing any consumer.
 *
 * {@link IoManager} implements this interface, so existing call sites are
 * unchanged — only the declared type of the dependency changes.
 *
 * <h3>Interface Segregation (ISP)</h3>
 * Only the raw key/mouse query methods are declared here — the minimal surface
 * that all consumers actually need.
 */
public interface IInputProvider {

    /**
     * Returns true while the specified key is held down.
     *
     * @param keycode A {@link com.badlogic.gdx.Input.Keys} constant.
     */
    boolean isKeyPressed(int keycode);

    /**
     * Returns true only on the single frame the specified key is first pressed.
     *
     * @param keycode A {@link com.badlogic.gdx.Input.Keys} constant.
     */
    boolean isKeyJustPressed(int keycode);

    /**
     * Returns true while the specified key is held down, using a named
     * {@link InputTrigger} binding instead of a raw keycode.
     *
     * @param trigger A named input trigger defined in {@link InputTrigger}.
     */
    boolean isPressed(InputTrigger trigger);

    /**
     * Returns true only on the single frame the specified trigger key is first
     * pressed, using a named {@link InputTrigger} binding.
     *
     * @param trigger A named input trigger defined in {@link InputTrigger}.
     */
    boolean isJustPressed(InputTrigger trigger);

    /** Returns true while the specified mouse button is held down. */
    boolean isMouseButtonPressed(int button);

    /** Returns true only on the single frame the mouse button is first pressed. */
    boolean isMouseButtonJustPressed(int button);

    /** Mouse X position in screen coordinates. */
    int getMouseX();

    /**
     * Mouse Y position, flipped so (0,0) is bottom-left to match LibGDX
     * world coordinates.
     */
    int getMouseY();
}
