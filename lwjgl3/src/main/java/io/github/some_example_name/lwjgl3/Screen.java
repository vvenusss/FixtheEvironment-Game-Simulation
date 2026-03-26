package io.github.some_example_name.lwjgl3;


public abstract class Screen implements IScreen {

    protected boolean       loaded        = false;

    protected EntityManager entityManager;

    public Screen() {
        entityManager = new EntityManager();
    }

    @Override
    public void onLoad() {
        loaded = true;
    }

    @Override
    public void onUnload() {
        entityManager.clear();
        loaded = false;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render() {

    }

    public void resize(int width, int height){}

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
