package io.github.some_example_name.lwjgl3;

public class SoundOptions {

	public float volume;
	public boolean loop;

	public SoundOptions() {
		this.volume = 1.0f;
		this.loop = false;
	}

	public SoundOptions(float volume, boolean loop) {
		this.volume = volume;
		this.loop = loop;
	}
}