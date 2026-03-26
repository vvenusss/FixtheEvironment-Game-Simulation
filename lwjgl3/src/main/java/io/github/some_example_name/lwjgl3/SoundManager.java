package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * SoundManager — Central authority for all audio playback and volume control.
 *
 * Single Responsibility (SRP): SoundManager owns all audio concerns: loading,
 * playback, volume, and mute. It exposes setters (setmusicVol, setsfxVol,
 * toggleMute) that callers use to adjust audio state. It does NOT read input
 * directly — that is the caller's responsibility (e.g., PauseScreen.update()
 * reads keys via IoManager and calls these setters).
 *
 * Open/Closed (OCP): New audio strategies (e.g., cross-fade, 3D positional
 * audio) can be added by extending SoundEffect / MusicTrack without modifying
 * this class.
 *
 * Dependency Inversion (DIP): SoundManager has no dependency on IoManager or
 * any input system. Callers are responsible for translating input into
 * volume/mute calls.
 */
public class SoundManager {

	private final Map<String, SoundEffect> sfx = new HashMap<>();
	private final Map<String, MusicTrack> musicTracks = new HashMap<>();

	private float sfxVol = 1.0f;
	private float musicVol = 1.0f;
	private boolean muted = false;

	// ── Load ──────────────────────────────────────────────────────────────────

	/**
	 * Load a sound effect.
	 *
	 * @param id       Key used to reference this sound (e.g., "sfx1").
	 * @param filePath Asset path relative to the assets folder (e.g., "sfx1.mp3").
	 */
	public void loadSound(String id, String filePath) {
		System.out.println("SoundManager: loading sound '" + id + "' from " + filePath);
		try {
			Sound gdxSound = Gdx.audio.newSound(Gdx.files.internal(filePath));
			AudioBuffer buffer = new AudioBuffer();
			SoundEffect effect = new SoundEffect(gdxSound, buffer, 0f);
			effect.setVolume(sfxVol);
			sfx.put(id, effect);
			System.out.println("  ✓ Sound '" + id + "' loaded.");
		} catch (Exception e) {
			System.out.println("  ✗ ERROR loading sound '" + id + "': " + e.getMessage());
		}
	}

	/**
	 * Load a music track with default MusicOptions (looping, full volume).
	 */
	public void loadMusic(String id, String filePath) {
		loadMusic(id, filePath, new MusicOptions());
	}

	/**
	 * Load a music track with explicit MusicOptions.
	 */
	public void loadMusic(String id, String filePath, MusicOptions options) {
		System.out.println("SoundManager: loading music '" + id + "' from " + filePath);
		try {
			Music gdxMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
			MusicTrack track = new MusicTrack(gdxMusic, options, 0f);
			track.setVolume(musicVol);
			musicTracks.put(id, track);
			System.out.println("  ✓ Music '" + id + "' loaded.");
		} catch (Exception e) {
			System.out.println("  ✗ ERROR loading music '" + id + "': " + e.getMessage());
		}
	}

	// ── Playback ──────────────────────────────────────────────────────────────

	/** Play a sound effect by ID. Respects mute state and current sfxVol. */
	public void playSound(String id) {
		if (muted) {
			System.out.println("SoundManager: cannot play '" + id + "' — muted.");
			return;
		}
		SoundEffect effect = sfx.get(id);
		if (effect != null) {
			effect.setVolume(sfxVol);
			effect.play(new SoundOptions(sfxVol, false));
			System.out.println("SoundManager: playing sound '" + id + "' at vol " + sfxVol);
		} else {
			System.out.println("SoundManager: sound '" + id + "' not found. Available: " + sfx.keySet());
		}
	}

	/** Play a music track by ID. Respects mute state and current musicVol. */
	public void playMusic(String id) {
		if (muted) {
			System.out.println("SoundManager: cannot play music '" + id + "' — muted.");
			return;
		}
		MusicTrack track = musicTracks.get(id);
		if (track != null) {
			track.setVolume(musicVol);
			track.play();
			System.out.println("SoundManager: playing music '" + id + "' at vol " + musicVol);
		} else {
			System.out.println("SoundManager: music '" + id + "' not found. Available: " + musicTracks.keySet());
		}
	}

	/**
	 * Pause all currently playing music tracks. SFX are NOT affected (per project
	 * rule).
	 */
	public void pauseMusic() {
		for (MusicTrack track : musicTracks.values()) {
			track.pause();
		}
		System.out.println("SoundManager: music paused.");
	}

	/**
	 * Resume all paused music tracks. Per project rule: only music resumes on
	 * unpause; SFX are not affected.
	 */
	public void resumeMusic() {
		if (muted) {
			System.out.println("SoundManager: cannot resume music — muted.");
			return;
		}
		for (MusicTrack track : musicTracks.values()) {
			track.resume();
		}
		System.out.println("SoundManager: music resumed.");
	}

	/** Stop a specific music track completely (not just pause). */
	public void stopMusic(String id) {
		MusicTrack track = musicTracks.get(id);
		if (track != null) {
			track.stop();
			System.out.println("SoundManager: stopped music '" + id + "'.");
		} else {
			System.out.println("SoundManager: music '" + id + "' not found.");
		}
	}

	// ── Volume ────────────────────────────────────────────────────────────────
	/**
	 * Set SFX volume (0.0–1.0). Saved immediately; applied to tracks only if not
	 * muted.
	 */
	public void setsfxVol(float volume) {
		sfxVol = clamp(volume);
		if (!muted) {
			for (SoundEffect e : sfx.values())
				e.setVolume(sfxVol);
		}
		System.out.println("SoundManager: SFX volume → " + (int) (sfxVol * 100) + "%");
	}

	/**
	 * Set music volume (0.0–1.0). Saved immediately; applied to tracks only if not
	 * muted.
	 */
	public void setmusicVol(float volume) {
		musicVol = clamp(volume);
		if (!muted) {
			for (MusicTrack t : musicTracks.values())
				t.setVolume(musicVol);
		}
		System.out.println("SoundManager: music volume → " + (int) (musicVol * 100) + "%");
	}

	public float getsfxVol() {
		return sfxVol;
	}

	public float getmusicVol() {
		return musicVol;
	}

	// ── Mute ─────────────────────────────────────────────────────────────────

	/**
	 * Toggle mute on/off. Uses volume-based muting (sets volumes to 0 / restores
	 * them) instead of pausing tracks, so it works correctly regardless of whether
	 * the game is paused and tracks are already in a paused state.
	 */
	public void toggleMute() {
		muted = !muted;
		if (muted) {
			for (MusicTrack t : musicTracks.values())
				t.setVolume(0f);
			for (SoundEffect e : sfx.values())
				e.setVolume(0f);
			System.out.println("SoundManager: MUTED.");
		} else {
			for (MusicTrack t : musicTracks.values())
				t.setVolume(musicVol);
			for (SoundEffect e : sfx.values())
				e.setVolume(sfxVol);
			System.out.println("SoundManager: UNMUTED.");
		}
	}

	public boolean isMuted() {
		return muted;
	}

	// ── Dispose ───────────────────────────────────────────────────────────────

	/** Dispose all audio resources. Call on application shutdown. */
	public void unloadAll() {
		for (SoundEffect e : sfx.values())
			e.dispose();
		for (MusicTrack t : musicTracks.values())
			t.dispose();
		sfx.clear();
		musicTracks.clear();
		System.out.println("SoundManager: all audio unloaded.");
	}

	// ── Private helpers ───────────────────────────────────────────────────────

	private float clamp(float v) {
		return Math.max(0f, Math.min(1f, v));
	}
}