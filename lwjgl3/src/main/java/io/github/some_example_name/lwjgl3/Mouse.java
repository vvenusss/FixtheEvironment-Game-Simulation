package io.github.some_example_name.lwjgl3;

// imports
import com.badlogic.gdx.Gdx;

// this class extends from AbstractInput to handle mouse-specific input 
public class Mouse extends AbstractInput {

	// mouse position state
	// stores the current mouse position 
	private final Vector2 mousePosition = new Vector2(0, 0);

	// constructs a mouse input handler with type MOUSE
	public Mouse() {
		super(InputType.MOUSE);
	}

	// polling update
	// this method should be called once per frame.
	// it retrieves the mouse position and adjusts the Y-coordinates so that (0,0)
	// correspond to the bottom left of the screen
	public void update() {
		float x = Gdx.input.getX();
		float y = Gdx.graphics.getHeight() - Gdx.input.getY();
		mousePosition.set(x, y);
	}

	// accessors
	// returns the current mouse position as a Vector2
	public Vector2 getMousePos() {
		return mousePosition;
	}

	// returns the current mouse X positon
	public float getMouseX() {
		return mousePosition.x;
	}

	// returns the current mouse Y position
	public float getMouseY() {
		return mousePosition.y;
	}
}