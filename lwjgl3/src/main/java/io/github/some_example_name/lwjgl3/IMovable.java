package io.github.some_example_name.lwjgl3;

/**
 * IMovable — Contract for any object that can be repositioned each frame.
 *
 * Implemented by UserMovement (uses InputHandler → Keyboard/Mouse for direction)
 * and can be implemented by AIMovement subclasses for polymorphic movement handling.
 */
public interface IMovable {
    void updatePosition(float dt);
    Vector2 getPosition();
    void setPosition(Vector2 p);
}
