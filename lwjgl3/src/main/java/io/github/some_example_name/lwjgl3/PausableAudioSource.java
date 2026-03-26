package io.github.some_example_name.lwjgl3;


public interface PausableAudioSource extends AudioSource {
    void pause();
    void resume();
    boolean isPlaying();
    boolean isPaused();
}
