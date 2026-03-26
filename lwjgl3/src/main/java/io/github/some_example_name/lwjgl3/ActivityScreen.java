package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * ActivityScreen — The main gameplay screen for FutureX: Fix the Environment.
 *
 * <h3>Responsibility (SRP)</h3> This screen orchestrates the game loop by
 * delegating to specialised subsystems. It does not own game logic directly:
 * <ul>
 * <li>Spawning and collision → {@link FallingObjectSpawner}</li>
 * <li>Background colour → {@link BackgroundManager} (via GameMaster)</li>
 * <li>Pollution bar → {@link PollutionBar}</li>
 * <li>Score → {@link ScoreManager}</li>
 * <li>Pause menu → {@link PauseScreen}</li>
 * <li>Entity lifecycle → {@link EntityManager} (inherited from Screen)</li>
 * </ul>
 *
 * <h3>Game-over conditions (pollution-only)</h3>
 * <ul>
 * <li>Pollution reaches 100 → lose → {@link EndScreen} with "GAME OVER".</li>
 * <li>Pollution reaches 0   → win  → {@link EndScreen} with "YOU WIN!".</li>
 * </ul>
 *
 * <h3>Input</h3> All input goes through {@link IInputProvider} — no direct
 * {@code Gdx.input} calls.
 */
public class ActivityScreen extends Screen {

	// ── Dependencies ──────────────────────────────────────────────────────────

	private final GameMaster gameMaster;

	// ── Rendering resources ───────────────────────────────────────────────────

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;

	protected OrthographicCamera camera;

	// ── Game entities ─────────────────────────────────────────────────────────

	/** The player-controlled bucket. */
	private Bucket bucket;

	// ── Game subsystems ───────────────────────────────────────────────────────

	private FallingObjectSpawner spawner;
	private PollutionBar pollutionBar;
	private ScoreManager scoreManager;
	private PauseScreen pauseScreen;

	// ── State ─────────────────────────────────────────────────────────────────

	/** Whether a game-over condition has been triggered this frame. */
	private boolean gameOverTriggered = false;

	/** Delay before transitioning to EndScreen after game-over (seconds). */
	private float gameOverTimer = 0f;
	private static final float GAME_OVER_DELAY = 1.5f;

	/** Whether the player won (true) or lost (false). */
	private boolean playerWon = false;

	/** Whether the game is currently paused. */
	private boolean paused = false;

	// ── Constructor ───────────────────────────────────────────────────────────

	public ActivityScreen(GameMaster gameMaster) {
		this.gameMaster = gameMaster;
	}

	// ── Lifecycle ─────────────────────────────────────────────────────────────

	@Override
	public void onLoad() {
		super.onLoad();

		float screenW = Gdx.graphics.getWidth();
		float screenH = Gdx.graphics.getHeight();

		// ── Rendering resources ───────────────────────────────────────────────
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.getData().setScale(1.2f);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenW, screenH);

		// ── Game subsystems ───────────────────────────────────────────────────
		pollutionBar = new PollutionBar();
		scoreManager = new ScoreManager();

		pauseScreen = new PauseScreen(gameMaster.getInputProvider(), gameMaster.getSoundManager(), font, camera);

		// ── Reset BackgroundManager for new session ───────────────────────────
		gameMaster.getBackgroundManager().setPollutionLevel(75f);

		// ── Bucket ───────────────────────────────────────────────────────────
		bucket = new Bucket(screenW / 2f - Bucket.DEFAULT_WIDTH / 2f, 40f,
				gameMaster.getInputProvider());
		entityManager.addEntity(bucket);

		// ── Spawner (no CityHealthBar passed) ────────────────────────────────
		spawner = new FallingObjectSpawner(entityManager, gameMaster.getBackgroundManager(),
				scoreManager);

		// ── State reset ───────────────────────────────────────────────────────
		gameOverTriggered = false;
		gameOverTimer = 0f;
		playerWon = false;
		paused = false;

		System.out.println("ActivityScreen loaded — FutureX game started.");
	}

    @Override
    public void resize(int width, int height) {

        camera.setToOrtho(false, width, height);
        batch.setProjectionMatrix(camera.combined);
    }

	@Override
	public void update(float deltaTime) {
		IInputProvider io = gameMaster.getInputProvider();
		float screenW = Gdx.graphics.getWidth();
		float screenH = Gdx.graphics.getHeight();

		// ── Pause toggle ──────────────────────────────────────────────────────
		if (io.isJustPressed(InputTrigger.KEY_PAUSE)) {
			paused = !paused;
		}

		// ── Quit to start screen (only while paused) ─────────────────────────
		if (paused && io.isJustPressed(InputTrigger.KEY_QUIT)) {
			paused = false;
			gameMaster.getScreenManager().setScreen(new StartScreen(gameMaster));
			return;
		}

		if (paused) {
			pauseScreen.update();
			return;
		}

		// ── Game-over delay ───────────────────────────────────────────────────
		if (gameOverTriggered) {
			gameOverTimer += deltaTime;
			if (gameOverTimer >= GAME_OVER_DELAY) {
				scoreManager.saveScoreToLeaderboard();
				gameMaster.getSoundManager().pauseMusic();
				gameMaster.getScreenManager()
						.setScreen(new EndScreen(gameMaster, playerWon, scoreManager.getScore()));
			}
			return;
		}


		// ── Entity update ─────────────────────────────────────────────────────
		entityManager.update(deltaTime);

		// ── Border enforcement ────────────────────────────────────────────────
		gameMaster.getCollisionManager().enforceBorders(entityManager.getEntitiesReadOnly(), screenW, screenH);

		// ── Spawner update ────────────────────────────────────────────────────
		spawner.update(deltaTime, bucket, screenW, screenH);

		// ── Win / lose check (pollution-based only) ───────────────────────────
		float pollution = gameMaster.getBackgroundManager().getPollutionLevel();

		if (pollution >= BackgroundManager.MAX_POLLUTION) {
			playerWon = false;
			gameOverTriggered = true;
			gameMaster.getSoundManager().playSound("sfx2");
			System.out.println("Game Over — pollution maxed out!");
		} else if (pollution <= BackgroundManager.MIN_POLLUTION) {
			playerWon = true;
			gameOverTriggered = true;
			gameMaster.getSoundManager().playSound("sfx1");
			System.out.println("You Win — environment fully cleaned!");
		}
	}

	@Override
	public void render() {
		float screenW = Gdx.graphics.getWidth();
		float screenH = Gdx.graphics.getHeight();

		camera.update();

		// ── Pollution haze overlay ────────────────────────────────────────────
		float pollution = gameMaster.getBackgroundManager().getPollutionLevel();
		float alpha = Math.min(0.65f, pollution / 120f);

		Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(
				com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
				com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.45f, 0.42f, 0.38f, alpha);
		shapeRenderer.rect(0, 0, screenW, screenH);
		shapeRenderer.end();

		Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

		// ── Entity shapes ─────────────────────────────────────────────────────
		shapeRenderer.setProjectionMatrix(camera.combined);
		entityManager.drawAll(batch, shapeRenderer);

		// ── HUD bar ───────────────────────────────────────────────────────────
		shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		pollutionBar.renderBar(shapeRenderer, gameMaster.getBackgroundManager(), screenH);
		shapeRenderer.end();

		// ── HUD text ──────────────────────────────────────────────────────────
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		pollutionBar.renderLabel(batch, font, gameMaster.getBackgroundManager(), screenH);

		// Score, speed level, map name
		font.setColor(Color.WHITE);
		font.draw(batch,
				"Score: " + scoreManager.getScore()
						+ "   " + gameMaster.getBackgroundManager().getTheme().getDisplayName(),
				10f, screenH - 55f);

		// Phase 2 indicator
		if (spawner != null && spawner.isPhase2Unlocked()) {
			font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
			font.draw(batch, "PHASE 2 New entities unlocked!", 10f, screenH - 75f);
		}

		// Controls hint — A/D and arrow keys
		font.setColor(new Color(1f, 1f, 1f, 0.6f));
		font.draw(batch, "A / [<]  Move Left       D / [>]  Move Right       P: Pause", 10f, 20f);

		batch.end();

		// ── Pause overlay ─────────────────────────────────────────────────────
		if (paused) {
			pauseScreen.render(batch, shapeRenderer);
		}
	}

	@Override
	public void onUnload() {
		if (spawner != null)
			spawner.dispose();
		if (batch != null)
			batch.dispose();
		if (shapeRenderer != null)
			shapeRenderer.dispose();
		if (font != null)
			font.dispose();
		super.onUnload();
	}
}
