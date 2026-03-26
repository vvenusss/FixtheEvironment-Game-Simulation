package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.EnumMap;
import java.util.Map;

/**
 * IoManager — Thin wrapper over the raw LibGDX input device.
 *
 * Single Responsibility (SRP):
 *   IoManager has exactly ONE responsibility: provide a clean, testable
 *   abstraction over Gdx.input so that no other class ever calls Gdx.input
 *   directly. It exposes raw key/mouse queries only.
 *
 * What IoManager does NOT do (moved to the correct owners):
 *   - Movement direction computation  → MovementManager.getMovementDirection()
 *   - Audio volume key detection      → SoundManager.applyVolumeInput(IoManager)
 *   - Mute key detection              → SoundManager.applyVolumeInput(IoManager)
 *
 * Pause detection remains here as a game-lifecycle query (not audio-specific).
 *
 * Open/Closed (OCP):
 *   New input queries can be added without modifying existing callers.
 *
 * Dependency Inversion (DIP):
 *   All managers receive IoManager via constructor injection; none call
 *   Gdx.input directly.
 */
public class IoManager implements IInputProvider {


    // ── InputTrigger → keycode bindings 

    private final Map<InputTrigger, Integer> keyBindings = new EnumMap<>(InputTrigger.class);

    public IoManager() {
        // Default key bindings
        keyBindings.put(InputTrigger.KEY_W,     Input.Keys.W);
        keyBindings.put(InputTrigger.KEY_A,     Input.Keys.A);
        keyBindings.put(InputTrigger.KEY_S,     Input.Keys.S);
        keyBindings.put(InputTrigger.KEY_D,     Input.Keys.D);
        keyBindings.put(InputTrigger.KEY_UP,    Input.Keys.UP);
        keyBindings.put(InputTrigger.KEY_DOWN,  Input.Keys.DOWN);
        keyBindings.put(InputTrigger.KEY_LEFT,  Input.Keys.LEFT);
        keyBindings.put(InputTrigger.KEY_RIGHT, Input.Keys.RIGHT);
        keyBindings.put(InputTrigger.KEY_PAUSE, Input.Keys.P);
        keyBindings.put(InputTrigger.KEY_QUIT,  Input.Keys.Q);
        keyBindings.put(InputTrigger.KEY_MUTE,  Input.Keys.M);
    }

    // isPressed & isJustPressed is passing in the inputTrigger enum type
    /** True while the bound key for this trigger is held down. */
    public boolean isPressed(InputTrigger trigger) {
        Integer keycode = keyBindings.get(trigger);
        return keycode != null && Gdx.input.isKeyPressed(keycode);
    }

    /** True only on the single frame the bound key is first pressed. */
    public boolean isJustPressed(InputTrigger trigger) {
        Integer keycode = keyBindings.get(trigger);
        return keycode != null && Gdx.input.isKeyJustPressed(keycode);
    }

    // ── Raw key queries ───────────────────────────────────────────────────────
    /** True while the key is held down. */
    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    /** True only on the single frame the key is first pressed. */
    public boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }

    // ── Raw mouse queries ─────────────────────────────────────────────────────
    public boolean isMouseButtonPressed(int button) {
        return Gdx.input.isButtonPressed(button);
    }

    public boolean isMouseButtonJustPressed(int button) {
        return Gdx.input.isButtonJustPressed(button);
    }

    /** Mouse X in screen coordinates. */
    public int getMouseX() {
        return Gdx.input.getX();
    }

    /**
     * Mouse Y flipped so (0,0) is bottom-left, matching LibGDX world coordinates.
     */
    public int getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }
}