package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * EndScreen — Displays the game result (win or lose) and final score.
 *
 * <h3>Changes from original EndScreen</h3>
 * <ul>
 *   <li>Accepts a {@code playerWon} boolean and {@code finalScore} int
 *       in the constructor to display the correct message and score.</li>
 *   <li>Adds a "Leaderboard" button that transitions to
 *       {@link LeaderboardScreen}.</li>
 *   <li>All other logic (Play Again, input via InputProvider) is unchanged.</li>
 * </ul>
 */
public class EndScreen extends Screen {

    // ── Layout constants ──────────────────────────────────────────────────────

    private static final float BUTTON_WIDTH  = 200f;
    private static final float BUTTON_HEIGHT = 50f;

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final GameMaster gameMaster;
    private final boolean    playerWon;
    private final int        finalScore;

    // ── Rendering resources ───────────────────────────────────────────────────

    private SpriteBatch batch;
    private BitmapFont  font;
    private GlyphLayout layout;

    // ── Button positions ──────────────────────────────────────────────────────

    private float playAgainX, playAgainY;
    private float leaderboardX, leaderboardY;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an EndScreen.
     *
     * @param gameMaster The game master.
     * @param playerWon  {@code true} if the player won; {@code false} if lost.
     * @param finalScore The score achieved this session.
     */
    public EndScreen(GameMaster gameMaster, boolean playerWon, int finalScore) {
        this.gameMaster  = gameMaster;
        this.playerWon   = playerWon;
        this.finalScore  = finalScore;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onLoad() {
        super.onLoad();
        batch  = new SpriteBatch();
        font   = new BitmapFont();
        font.getData().setScale(1.5f);
        layout = new GlyphLayout();

        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        playAgainX  = cx - BUTTON_WIDTH / 2f;
        playAgainY  = cy - 80f;
        leaderboardX = cx - BUTTON_WIDTH / 2f;
        leaderboardY = cy - 150f;

        gameMaster.getSoundManager().resumeMusic();
        System.out.println("EndScreen loaded. Won=" + playerWon + " Score=" + finalScore);
    }

    @Override
    public void update(float deltaTime) {
        IInputProvider io = gameMaster.getInputProvider();
        if (!io.isMouseButtonJustPressed(0)) return;

        float mx = io.getMouseX();
        float my = io.getMouseY();

        // Play Again → MapSelectScreen
        if (mx >= playAgainX && mx <= playAgainX + BUTTON_WIDTH &&
            my >= playAgainY && my <= playAgainY + BUTTON_HEIGHT) {
            gameMaster.getSoundManager().playSound("sfx1");
            gameMaster.getScreenManager().setScreen(new MapSelectScreen(gameMaster));
            return;
        }

        // Leaderboard
        if (mx >= leaderboardX && mx <= leaderboardX + BUTTON_WIDTH &&
            my >= leaderboardY && my <= leaderboardY + BUTTON_HEIGHT) {
            gameMaster.getSoundManager().playSound("sfx1");
            gameMaster.getScreenManager().setScreen(
                new LeaderboardScreen(gameMaster));
        }
    }

    @Override
    public void resize(int width, int height) {
        float cx = width  / 2f;
        float cy = height / 2f;
        playAgainX   = cx - BUTTON_WIDTH / 2f;
        playAgainY   = cy - 80f;
        leaderboardX = cx - BUTTON_WIDTH / 2f;
        leaderboardY = cy - 150f;

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

    }

    @Override
    public void render() {
        batch.begin();

        // Title
        font.setColor(playerWon ? Color.GREEN : Color.RED);
        String title = playerWon ? "YOU WIN!" : "GAME OVER";
        layout.setText(font, title);
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() / 2f + 80f);

        // Score
        font.setColor(Color.WHITE);
        layout.setText(font, "Score: " + finalScore);
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() / 2f + 30f);

        // Play Again button
        font.setColor(Color.YELLOW);
        layout.setText(font, "PLAY AGAIN");
        font.draw(batch, layout,
            playAgainX + (BUTTON_WIDTH  - layout.width)  / 2f,
            playAgainY + (BUTTON_HEIGHT + layout.height) / 2f);

        // Leaderboard button
        font.setColor(Color.CYAN);
        layout.setText(font, "LEADERBOARD");
        font.draw(batch, layout,
            leaderboardX + (BUTTON_WIDTH  - layout.width)  / 2f,
            leaderboardY + (BUTTON_HEIGHT + layout.height) / 2f);

        batch.end();
    }

    @Override
    public void onUnload() {
        if (batch  != null) batch.dispose();
        if (font   != null) font.dispose();
        super.onUnload();
    }
}
