package io.github.some_example_name.lwjgl3;

import java.util.Stack;


public class ScreenManager {

    private final Stack<IScreen> screenStack = new Stack<>();


    public void pushScreen(IScreen screen) {
        if (!screenStack.isEmpty()) screenStack.peek().onUnload();
        screenStack.push(screen);
        screen.onLoad();
    }


    public void popScreen() {
        if (!screenStack.isEmpty()) screenStack.pop().onUnload();
        if (!screenStack.isEmpty()) screenStack.peek().onLoad();
    }


    public void setScreen(IScreen screen) {
        while (!screenStack.isEmpty()) screenStack.pop().onUnload();
        pushScreen(screen);
    }


    public void update(float deltaTime) {
        if (!screenStack.isEmpty()) screenStack.peek().update(deltaTime);
    }
    public void resize(int width, int height){
        if(!screenStack.isEmpty()){
            screenStack.peek().resize(width, height);
        }
    }


    public void render() {
        if (!screenStack.isEmpty()) screenStack.peek().render();
    }


    public void dispose() {
        if (!screenStack.isEmpty()) screenStack.pop().onUnload();
    }
}
