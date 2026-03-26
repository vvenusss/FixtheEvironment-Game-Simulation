package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.audio.Music;


public class MusicTrack implements PausableAudioSource {

    private final Music  gdxMusic;
    private final float  duration;


    private boolean paused = false;

    public MusicTrack(Music gdxMusic, MusicOptions options, float duration) {
        this.gdxMusic = gdxMusic;
        this.duration = duration;
        if (gdxMusic != null) {
            gdxMusic.setLooping(options.loop);
            gdxMusic.setVolume(options.volume);
        }
    }

    // ── PausableAudioSource ───────────────────────────────────────────────────

    @Override
    public void play() {
        if (gdxMusic == null) return;
        paused = false;
        gdxMusic.play();
    }

    @Override
    public void stop() {
        if (gdxMusic == null) return;
        paused = false;
        gdxMusic.stop();
    }

    @Override
    public void pause() {
        if (gdxMusic == null) return;

        if (gdxMusic.isPlaying()) {
            gdxMusic.pause();
            paused = true;
        }
    }

    @Override
    public void resume() {
        if (gdxMusic == null) return;

        if (paused && !gdxMusic.isPlaying()) {
            gdxMusic.play();
            paused = false;
        }
    }

    @Override
    public void setVolume(float volume) {
        if (gdxMusic != null) {
            gdxMusic.setVolume(Math.max(0f, Math.min(1f, volume)));
        }
    }

    @Override
    public boolean isPlaying() {
        return gdxMusic != null && gdxMusic.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public float getDuration() { return duration; }

    public void dispose() {
        if (gdxMusic != null) gdxMusic.dispose();
    }
}
