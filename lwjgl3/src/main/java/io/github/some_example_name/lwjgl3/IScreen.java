package io.github.some_example_name.lwjgl3;


public interface IScreen {
    void onLoad();
    void onUnload();
    void update(float deltaTime);
    void render();
    void resize(int width, int height);
}
