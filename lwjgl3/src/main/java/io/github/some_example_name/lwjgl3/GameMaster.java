package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 * GameMaster — The top-level application controller for FutureX.
 *
 * <h3>Responsibility (SRP)</h3>
 * GameMaster owns the lifecycle of all game-wide managers. It does not
 * contain game logic — that is delegated to the active {@link Screen}.
 *
 * <h3>Changes from Part 1</h3>
 * <ul>
 *   <li>Added {@link BackgroundManager} — owned here so all screens can
 *       read the current background colour.</li>
 *   <li>{@code render()} reads the background colour from
 *       {@link BackgroundManager} instead of a hardcoded constant.</li>
 *   <li>{@code StartScreen} now navigates to {@link MapSelectScreen} so
 *       the player can choose a map theme before the game starts.</li>
 * </ul>
 *
 * <h3>Manager initialisation (feedback point 23)</h3>
 * All managers that do NOT require LibGDX to be fully initialised are
 * created in the constructor. Managers that depend on {@code Gdx.graphics}
 * (e.g. {@link CollisionManager}) are created in {@link #create()}.
 */
public class GameMaster extends ApplicationAdapter {

    // ── Managers ──────────────────────────────────────────────────────────────

    private ScreenManager     screenManager;
    private ICollisionManager collisionManager;
    private IoManager         ioManager;
    private MovementManager   movementManager;
    private SoundManager      soundManager;

    /** Owns the dynamic background colour driven by pollution level. */
    private BackgroundManager backgroundManager;

    private boolean paused = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs GameMaster and initialises all managers that do not require
     * LibGDX to be fully started (i.e. no {@code Gdx.graphics} calls here).
     *
     * Managers initialised here:
     * <ul>
     *   <li>{@link IoManager}</li>
     *   <li>{@link MovementManager} (depends on {@link IInputProvider})</li>
     *   <li>{@link SoundManager}</li>
     *   <li>{@link ScreenManager}</li>
     *   <li>{@link BackgroundManager} (default theme: CITY)</li>
     * </ul>
     */
    public GameMaster() {
        ioManager         = new IoManager();
        movementManager   = new MovementManager(ioManager);   // IoManager implements IInputProvider
        soundManager      = new SoundManager();
        screenManager     = new ScreenManager();
        backgroundManager = new BackgroundManager(MapTheme.CITY);
        System.out.println("=== GameMaster Constructor ===");
        System.out.println("✓ IoManager, MovementManager, SoundManager, ScreenManager, BackgroundManager ready.");
    }

    public void resize(int width, int height){
        screenManager.resize(width, height);
    }

    // ── LibGDX lifecycle ──────────────────────────────────────────────────────

    /**
     * Called by LibGDX after the OpenGL context is ready.
     * Only LibGDX-dependent initialisation happens here.
     */
    @Override
    public void create() {
        System.out.println("=== GameMaster.create() ===");

        // CollisionManager needs Gdx.graphics dimensions — must be here.
        // Arguments: minX, maxX, minY, maxY, thresholdDetection, responseType
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        collisionManager = new CollisionManager(
            0f,          // minX — left border
            w,           // maxX — right border
            0f,          // minY — bottom border
            h,           // maxY — top border
            0f,          // thresholdDetection — overlap threshold (0 = exact touch)
            "bounce",     // responseType — passed to CollisionHandling
            new BounceCollisionStrategy()  // DIP: strategy injected, not created inside
        );
        System.out.println("✓ CollisionManager initialised");

        // Load audio assets (requires LibGDX audio system).
        soundManager.loadMusic("background", "elevatormusic.mp3");
        soundManager.playMusic("background");
        soundManager.loadSound("sfx1", "sfx1.mp3");
        soundManager.loadSound("sfx2", "sfx2.mp3");
        System.out.println("✓ SoundManager assets loaded");

        // Start on the title screen.
        screenManager.setScreen(new StartScreen(this));
        System.out.println("✓ StartScreen set\n");
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        // Background colour is driven by BackgroundManager (pollution level).
        // Individual screens may override this via glClearColor in their render().
        Color bg = backgroundManager.getBackgroundColor();
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        syncCollisionBounds();

        if (!paused && movementManager != null) movementManager.update(dt);
        if (screenManager != null) screenManager.update(dt);
        if (screenManager != null) screenManager.render();
    }

    @Override
    public void dispose() {
        System.out.println("\n=== GameMaster Shutdown ===");
        if (soundManager     != null) soundManager.unloadAll();
        if (screenManager    != null) screenManager.dispose();
        if (collisionManager != null) collisionManager.clearCollisions();
        if (movementManager  != null) movementManager.clear();
    }

    // ── Pause / Resume ────────────────────────────────────────────────────────

    public void pause() {
        if (!paused) {
            paused = true;
            soundManager.pauseMusic();
            System.out.println("Game paused.");
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            soundManager.resumeMusic();
            System.out.println("Game resumed.");
        }
    }

    public boolean isPaused() { return paused; }

    // ── Public accessors ──────────────────────────────────────────────────────

    public ScreenManager     getScreenManager()    { return screenManager; }
    public ICollisionManager getCollisionManager() { return collisionManager; }
    /** Returns the input provider interface — callers should depend on {@link IInputProvider}, not the concrete {@link IoManager}. */
    public IInputProvider    getInputProvider()        { return ioManager; }
    public MovementManager   getMovementManager()  { return movementManager; }
    public SoundManager      getSoundManager()     { return soundManager; }

    /**
     * Returns the {@link BackgroundManager} that drives the dynamic
     * background colour based on the current pollution level.
     * Used by {@link ActivityScreen} and {@link BackgroundManager}.
     */
    public BackgroundManager getBackgroundManager() { return backgroundManager; }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Keeps CollisionManager bounds in sync with the current window size. */
    private void syncCollisionBounds() {
        if (collisionManager != null) {
            collisionManager.resize(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        }
    }
}
