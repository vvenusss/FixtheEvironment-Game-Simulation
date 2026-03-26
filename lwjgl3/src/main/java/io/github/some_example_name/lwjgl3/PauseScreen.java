package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PauseScreen {

	private final IInputProvider inputProvider;
	private final SoundManager soundManager;
	private final BitmapFont font;
	private final OrthographicCamera camera;

	private static final float VOLUME_STEP = 0.1f;

	public PauseScreen(IInputProvider inputProvider, SoundManager soundManager, BitmapFont font, OrthographicCamera camera) {
		this.inputProvider = inputProvider;
		this.soundManager = soundManager;
		this.font = font;
		this.camera = camera;
	}

	/**
	 * Process pause-screen input: volume adjustment and mute toggle. Called by
	 * ActivityScreen each frame while paused.
	 *
	 * SRP: PauseScreen owns all pause-specific input (volume, mute). ActivityScreen
	 * owns pause/quit toggling.
	 */
	public void update() {
		// ── Music volume: UP / DOWN ──────────────────────────────────────────
		if (inputProvider.isJustPressed(InputTrigger.KEY_UP)) {
			soundManager.setmusicVol(soundManager.getmusicVol() + VOLUME_STEP);
		}
		if (inputProvider.isJustPressed(InputTrigger.KEY_DOWN)) {
			soundManager.setmusicVol(soundManager.getmusicVol() - VOLUME_STEP);
		}

		// ── SFX volume: LEFT / RIGHT ─────────────────────────────────────────
		if (inputProvider.isJustPressed(InputTrigger.KEY_RIGHT)) {
			soundManager.setsfxVol(soundManager.getsfxVol() + VOLUME_STEP);
		}
		if (inputProvider.isJustPressed(InputTrigger.KEY_LEFT)) {
			soundManager.setsfxVol(soundManager.getsfxVol() - VOLUME_STEP);
		}

		// ── Mute toggle: M ───────────────────────────────────────────────────
		if (inputProvider.isJustPressed(InputTrigger.KEY_MUTE)) {
			soundManager.toggleMute();
		}
	}

	public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 0.7f);
		shapeRenderer.rect(0, 0, w, h);
		shapeRenderer.end();

		batch.begin();

		font.getData().setScale(3.0f);
		font.setColor(Color.YELLOW);
		font.draw(batch, "PAUSED", w / 2f - 80, h / 2f + 100);

		font.getData().setScale(1.0f);
		font.setColor(Color.WHITE);
		float cx = w / 2f - 150;
		float cy = h / 2f;
		font.draw(batch, "UP / DOWN  : Music Volume (" + pct(soundManager.getmusicVol()) + "%)", cx, cy);
		font.draw(batch, "LEFT / RIGHT: SFX Volume  (" + pct(soundManager.getsfxVol()) + "%)", cx, cy - 30);
		font.draw(batch, "M          : Mute / Unmute " + (soundManager.isMuted() ? "[MUTED]" : "[ON]"), cx, cy - 60);
		font.draw(batch, "P          : Resume", cx, cy - 90);
		font.draw(batch, "Q          : Quit to Menu", cx, cy - 120);

		font.getData().setScale(1.5f);
		batch.end();
	}

	private int pct(float v) {
		return (int) (v * 100);
	}
}