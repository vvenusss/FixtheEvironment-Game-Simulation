package io.github.some_example_name.lwjgl3;

import java.util.HashSet;
import java.util.Set;

/**
 * UpwardMovement
 *
 * Records which falling objects have bounced off the bucket/paddle.
 *
 * Purpose:
 * - Avoid modifying FallingObject
 * - Avoid double-bouncing the same object every frame
 * - Provide a clean integration point for teammates
 */
public class UpwardMovement {

    /** Stores all falling objects that have already bounced. */
    private final Set<FallingObject> bouncedObjects = new HashSet<>();

    /**
     * Returns true if this object has already been registered as bounced.
     */
    public boolean hasBounced(FallingObject object) {
        return object != null && bouncedObjects.contains(object);
    }

    /**
     * Registers this object as bounced.
     */
    public void registerBounce(FallingObject object) {
        if (object != null) {
            bouncedObjects.add(object);
        }
    }

    /**
     * Removes this object from bounce tracking.
     * Call this when the object is destroyed/removed from the game.
     */
    public void unregisterBounce(FallingObject object) {
        if (object != null) {
            bouncedObjects.remove(object);
        }
    }

    /**
     * Clears all bounce records.
     * Useful when restarting a level or unloading a screen.
     */
    public void clear() {
        bouncedObjects.clear();
    }

    /**
     * Registers the bounce and immediately flips the object upward.
     *
     * Returns true only if the bounce was newly registered.
     * Returns false if the object had already bounced before.
     */
    public boolean registerAndBounceUp(FallingObject object) {
        if (object == null) return false;
        if (hasBounced(object)) return false;

        registerBounce(object);

        // Falling objects normally have negative Y velocity.
        // Make it positive so the object moves upward.
        object.setVelocityY(Math.abs(object.getVelocityY()));

        return true;
    }

    /**
     * Returns true if a bounced object has moved fully above the top boundary.
     */
    public boolean hasExitedTop(FallingObject object, float screenHeight) {
        if (object == null) return false;
        return hasBounced(object) && object.getY() > screenHeight;
    }
}