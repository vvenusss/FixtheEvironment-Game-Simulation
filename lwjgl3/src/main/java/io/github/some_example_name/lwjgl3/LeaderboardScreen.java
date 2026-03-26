package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

/**
 * LeaderboardScreen — Displays the top 10 all-time scores.
 *
 * <h3>Responsibility (SRP)</h3>
 * This screen only renders the leaderboard. Score persistence is owned
 * by {@link ScoreManager}. A "Back" button returns to {@link StartScreen}.
 */
public class LeaderboardScreen extends Screen {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final GameMaster gameMaster;

    // ── Rendering resources ───────────────────────────────────────────────────

    private SpriteBatch batch;
    private BitmapFont  font;
    private GlyphLayout layout;

    // ── Scores ────────────────────────────────────────────────────────────────

    private List<Integer> scores;

    // ── Back button ───────────────────────────────────────────────────────────

    private static final float BTN_WIDTH  = 150f;
    private static final float BTN_HEIGHT = 40f;
    private float backBtnX, backBtnY;

    // ── Constructor ───────────────────────────────────────────────────────────

    public LeaderboardScreen(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onLoad() {
        super.onLoad();
        batch  = new SpriteBatch();
        font   = new BitmapFont();
        font.getData().setScale(1.3f);
        layout = new GlyphLayout();

        // Load scores from a temporary ScoreManager (reads from Preferences).
        ScoreManager sm = new ScoreManager();
        scores = sm.getLeaderboard();

        backBtnX = (Gdx.graphics.getWidth() - BTN_WIDTH) / 2f;
        backBtnY = 30f;

        System.out.println("LeaderboardScreen loaded. Entries: " + scores.size());
    }

    @Override
    public void update(float deltaTime) {
        IInputProvider io = gameMaster.getInputProvider();
        if (io.isMouseButtonJustPressed(0)) {
            float mx = io.getMouseX();
            float my = io.getMouseY();
            if (mx >= backBtnX && mx <= backBtnX + BTN_WIDTH &&
                my >= backBtnY && my <= backBtnY + BTN_HEIGHT) {
                gameMaster.getScreenManager().setScreen(new StartScreen(gameMaster));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        backBtnX = (width - BTN_WIDTH) / 2f;
        backBtnY = 30f;

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

    }

    @Override
    public void render() {
        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        batch.begin();

        // Title
        font.setColor(Color.YELLOW);
        layout.setText(font, "LEADERBOARD");
        font.draw(batch, layout, cx - layout.width / 2f, cy + 200f);

        // Score entries
        font.getData().setScale(1.1f);
        for (int i = 0; i < scores.size(); i++) {
            font.setColor(i == 0 ? Color.GOLD : Color.WHITE);
            String entry = (i + 1) + ".   " + scores.get(i) + " pts";
            layout.setText(font, entry);
            font.draw(batch, layout, cx - layout.width / 2f, cy + 150f - i * 35f);
        }

        if (scores.isEmpty()) {
            font.setColor(Color.LIGHT_GRAY);
            layout.setText(font, "No scores yet. Play a game first!");
            font.draw(batch, layout, cx - layout.width / 2f, cy);
        }

        // Back button
        font.getData().setScale(1.2f);
        font.setColor(Color.CYAN);
        layout.setText(font, "BACK");
        font.draw(batch, layout,
            backBtnX + (BTN_WIDTH  - layout.width)  / 2f,
            backBtnY + (BTN_HEIGHT + layout.height) / 2f);

        batch.end();
    }

    @Override
    public void onUnload() {
        if (batch  != null) batch.dispose();
        if (font   != null) font.dispose();
        super.onUnload();
    }
}
