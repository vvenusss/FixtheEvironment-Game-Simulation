package io.github.some_example_name.lwjgl3;

public class InputEvent {

	private final InputType type;
	private final InputTrigger trigger;

	public InputEvent(InputType type, InputTrigger trigger) {
		this.type = type;
		this.trigger = trigger;
	}

	public InputType getType() {
		return type;
	}

	public InputTrigger getTrigger() {
		return trigger;
	}
}