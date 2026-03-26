package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.audio.Sound;

public class SoundEffect implements AudioSource {

	private final Sound gdxSound;
	private final AudioBuffer buffer;
	private final float duration;
	private float volume = 1.0f;

	public SoundEffect(Sound gdxSound, AudioBuffer buffer, float duration) {
		this.gdxSound = gdxSound;
		this.buffer = buffer;
		this.duration = duration;
	}

	// ── AudioSource ───────────────────────────────────────────────────────────

	@Override
	public void play() {
		if (gdxSound != null)
			gdxSound.play(volume);
	}

	public void play(SoundOptions options) {
		if (gdxSound == null)
			return;
		if (options.loop) {
			gdxSound.loop(options.volume);
		} else {
			gdxSound.play(options.volume);
		}
	}

	@Override
	public void stop() {
		if (gdxSound != null)
			gdxSound.stop();
	}

	@Override
	public void setVolume(float volume) {
		this.volume = Math.max(0f, Math.min(1f, volume));
	}

	// ── Accessors ─────────────────────────────────────────────────────────────

	public boolean isLoaded() {
		return gdxSound != null;
	}

	public float getDuration() {
		return duration;
	}

	public AudioBuffer getBuffer() {
		return buffer;
	}

	public void dispose() {
		if (gdxSound != null)
			gdxSound.dispose();
	}
}
