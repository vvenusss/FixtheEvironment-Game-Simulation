package io.github.some_example_name.lwjgl3;


public class MusicOptions {

    public float   volume;
    public boolean loop;

    public MusicOptions() {
        this.volume = 1.0f;
        this.loop   = true;
    }

    public MusicOptions(float volume, boolean loop) {
        this.volume = volume;
        this.loop   = loop;
    }
}
