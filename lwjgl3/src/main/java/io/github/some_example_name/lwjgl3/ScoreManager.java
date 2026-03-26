package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScoreManager — Tracks the current game score and persists high scores.
 *
 * <h3>Responsibility (SRP)</h3>
 * This class owns exactly two concerns:
 * <ol>
 *   <li>Tracking the current session score. Score only ever increases.</li>
 *   <li>Persisting the top 10 high scores across sessions using LibGDX
 *       {@link Preferences} (backed by a local file on desktop).</li>
 * </ol>
 *
 * <h3>Scoring rules</h3>
 * <ul>
 *   <li>Bad entity bounced and exits top: +5 points.</li>
 *   <li>Good entity exits bottom (not bounced): +5 points, pollution -5%.</li>
 *   <li>Score never decreases.</li>
 * </ul>
 */
public class ScoreManager {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String PREFS_NAME       = "futurex_scores";
    private static final String KEY_PREFIX       = "score_";
    private static final int    MAX_LEADERBOARD  = 10;

    /** Points awarded when any bounced object exits the top of the screen. */
    public static final int POINTS_BOUNCE_EXIT_TOP = 5;

    // ── State ─────────────────────────────────────────────────────────────────

    /** Current session score. Never goes below 0. */
    private int score = 0;

    // ── Score manipulation ────────────────────────────────────────────

    /**
     * Adds points to the current score. Score is clamped to 0 (never negative).
     *
     * @param points Points to add.
     */
    public void addPoints(int points) {
        score = Math.max(0, score + points);
    }

       // ── Accessors ───────────────────────────────────────────────────

    /** Returns the current session score. */
    public int getScore() { return score; }

    // ── Leaderboard persistence ───────────────────────────────────────────────

    /**
     * Saves the current session score to the persistent leaderboard if it
     * qualifies for the top 10. Should be called when the game ends.
     */
    public void saveScoreToLeaderboard() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        List<Integer> scores = loadAllScores(prefs);
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());

        // Keep only the top MAX_LEADERBOARD scores.
        while (scores.size() > MAX_LEADERBOARD) {
            scores.remove(scores.size() - 1);
        }

        // Persist back to preferences.
        for (int i = 0; i < scores.size(); i++) {
            prefs.putInteger(KEY_PREFIX + i, scores.get(i));
        }
        prefs.flush();
        System.out.println("[ScoreManager] Score " + score + " saved to leaderboard.");
    }

    /**
     * Loads the top 10 scores from persistent storage, sorted descending.
     *
     * @return A list of up to {@value #MAX_LEADERBOARD} integer scores.
     */
    public List<Integer> getLeaderboard() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return loadAllScores(prefs);
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /** Resets the current session score. */
    public void reset() {
        score = 0;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private List<Integer> loadAllScores(Preferences prefs) {
        List<Integer> scores = new ArrayList<>();
        for (int i = 0; i < MAX_LEADERBOARD; i++) {
            int s = prefs.getInteger(KEY_PREFIX + i, -1);
            if (s >= 0) scores.add(s);
        }
        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }
}
