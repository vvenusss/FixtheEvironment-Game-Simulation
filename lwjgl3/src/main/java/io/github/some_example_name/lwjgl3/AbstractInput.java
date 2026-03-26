package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInput {

	private InputType inputDeviceType;
	private Map<InputTrigger, Action> inputMap;

	public AbstractInput(InputType type) {
		this.inputDeviceType = type;
		this.inputMap = new HashMap<>();
	}

	public void registerInput(InputTrigger inputTrigger, Action action) {
		inputMap.put(inputTrigger, action);
	}

	public void handleInputEvent(InputEvent inputEvent) {
		if (inputEvent == null || inputEvent.getType() != inputDeviceType)
			return;
		Action action = inputMap.get(inputEvent.getTrigger());
		if (action != null)
			action.execute();
	}
}