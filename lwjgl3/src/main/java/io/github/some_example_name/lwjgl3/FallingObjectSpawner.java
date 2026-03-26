package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * FallingObjectSpawner — Spawns, culls, and resolves collisions for all
 * falling objects in FutureX: Fix the Environment.
 *
 * <h3>Gameplay rules</h3>
 *
 * <b>ALL entities bounce off the paddle</b> — good and bad alike.
 * The outcome is determined only after the object exits the screen:
 *
 * <table border="1">
 *   <tr><th>Event</th><th>Green Solution</th><th>Pollution Object</th></tr>
 *   <tr>
 *     <td>Bucket touches object (bounced)</td>
 *     <td>Bounced upward — no effect yet</td>
 *     <td>Bounced upward — no effect yet</td>
 *   </tr>
 *   <tr>
 *     <td>Object exits TOP (after bounce)</td>
 *     <td>Score +5, pollution unchanged</td>
 *     <td>Score +5, pollution unchanged</td>
 *   </tr>
 *   <tr>
 *     <td>Object exits BOTTOM (not bounced)</td>
 *     <td>Pollution -5%, score unchanged</td>
 *     <td>No change to score or pollution</td>
 *   </tr>
 * </table>
 *
 * <h3>Phase system</h3>
 * Phase 2 unlocks when pollution drops to or below
 * {@value #PHASE2_POLLUTION_THRESHOLD}%.
 *
 * <h3>Map-specific spawn tables</h3>
 * <table border="1">
 *   <tr><th>Map</th><th>Phase</th><th>Good</th><th>Bad</th></tr>
 *   <tr><td>City</td><td>1</td><td>tree.png</td><td>garbage.png</td></tr>
 *   <tr><td>City</td><td>2</td><td>tree.png, recycling.png</td><td>garbage.png, poop.png, battery.png</td></tr>
 *   <tr><td>Beach</td><td>1</td><td>turtle.png</td><td>cigarette.png, bottle.png</td></tr>
 *   <tr><td>Beach</td><td>2</td><td>turtle.png, shell.png, wood.png</td><td>cigarette.png, bottle.png, banana.png</td></tr>
 * </table>
 */
public class FallingObjectSpawner {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final float BASE_FALL_SPEED     = 150f;
    private static final float BASE_SPAWN_INTERVAL = 1.2f;
    private static final float OBJECT_SIZE         = 48f;

    /** Fraction of spawns that are green solutions (40%). */
    private static final float GREEN_RATIO = 0.40f;

    // ── Phase 2 unlock threshold ──────────────────────────────────────────────

    private static final float PHASE2_POLLUTION_THRESHOLD = 50f;

    // ── Game-effect constants ─────────────────────────────────────────────────

    /** Pollution decrease when a good object exits the BOTTOM (falls through). */
    private static final float POLLUTION_BOTTOM_GOOD  = 5f;
    /** Pollution increase when a bad object exits the BOTTOM (not bounced). */
    private static final float POLLUTION_BOTTOM_BAD   = 5f;

    /** Score awarded when ANY bounced object exits the TOP. */
    private static final int SCORE_BOUNCE_EXIT_TOP = 5;

    /**
     * Fixed fall speed in pixels per second.
     */
    private static final float FALL_SPEED = 120f;

    // ── City spawn tables ─────────────────────────────────────────────────────

    private static final SpawnOption[] CITY_GOOD_P1 = {
        new SpawnOption(ObjectType.TREE, "tree.png")
    };

    private static final SpawnOption[] CITY_GOOD_P2 = {
        new SpawnOption(ObjectType.TREE, "tree.png"),
        new SpawnOption(ObjectType.RECYCLING_BIN, "recycling.png")
    };

    private static final SpawnOption[] CITY_BAD_P1 = {
        new SpawnOption(ObjectType.FACTORY, "garbage.png")
    };

    private static final SpawnOption[] CITY_BAD_P2 = {
        new SpawnOption(ObjectType.FACTORY, "garbage.png"),
        new SpawnOption(ObjectType.TRASH, "poop.png"),
        new SpawnOption(ObjectType.BATTERY, "battery.png")
    };

    // ── Beach spawn tables ────────────────────────────────────────────────────

    private static final SpawnOption[] BEACH_GOOD_P1 = {
        new SpawnOption(ObjectType.TREE, "turtle.png")
    };

    private static final SpawnOption[] BEACH_GOOD_P2 = {
        new SpawnOption(ObjectType.TREE, "turtle.png"),
        new SpawnOption(ObjectType.RECYCLING_BIN, "shell.png"),
        new SpawnOption(ObjectType.WOOD, "wood.png")
    };

    private static final SpawnOption[] BEACH_BAD_P1 = {
        new SpawnOption(ObjectType.CAR, "cigarette.png"),
        new SpawnOption(ObjectType.FACTORY, "bottle.png")
    };

    private static final SpawnOption[] BEACH_BAD_P2 = {
        new SpawnOption(ObjectType.CAR, "cigarette.png"),
        new SpawnOption(ObjectType.FACTORY, "bottle.png"),
        new SpawnOption(ObjectType.TRASH, "banana.png")
    };

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final EntityManager entityManager;
    private final BackgroundManager bgManager;
    private final ScoreManager scoreManager;
    private final UpwardMovement upMovement = new UpwardMovement();
    private final BounceCollisionStrategy bounceCollisionStrategy = new BounceCollisionStrategy();

    // ── State ─────────────────────────────────────────────────────────────────

    private final List<FallingObject> activeObjects = new ArrayList<>();
    private final Random random = new Random();
    private float spawnTimer = 0f;

    /** True once Phase 2 has been unlocked this session. */
    private boolean phase2Unlocked = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    public FallingObjectSpawner(EntityManager entityManager,
                                BackgroundManager bgManager,
                                ScoreManager scoreManager) {
        this.entityManager = entityManager;
        this.bgManager = bgManager;
        this.scoreManager = scoreManager;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void update(float deltaTime, Bucket bucket, float screenW, float screenH) {

        // ── Phase 2 check ─────────────────────────────────────────────────────
        checkPhase2Unlock();

        // ── Spawn ──────────────────────────────────────────────────────────────
        spawnTimer += deltaTime;
        if (spawnTimer >= BASE_SPAWN_INTERVAL) {
            spawnTimer -= BASE_SPAWN_INTERVAL;
            spawnObject(screenW, screenH);
        }

        // ── Collision + cull ──────────────────────────────────────────────────
        List<FallingObject> toRemove = new ArrayList<>();

        for (FallingObject obj : activeObjects) {

            boolean alreadyBounced = upMovement.hasBounced(obj);

            if (!alreadyBounced && bucket != null && obj.collidesWith(bucket)) {
                upMovement.registerAndBounceUp(obj);
                Collision collision = new Collision(bucket, obj);
                bounceCollisionStrategy.resolve(collision);

            } else if (alreadyBounced && upMovement.hasExitedTop(obj, screenH)) {
                if (!(obj instanceof GreenSolution)) {
                    applyBadBounceExitTop();
                } else{
                    bgManager.increasePollution(POLLUTION_BOTTOM_GOOD);
                }
                toRemove.add(obj);

            } else if (!alreadyBounced && obj.isOffScreen()) {
                applyMissedGround(obj);
                toRemove.add(obj);
            }
        }

        for (FallingObject obj : toRemove) {
            upMovement.unregisterBounce(obj);
            activeObjects.remove(obj);
            entityManager.removeEntity(obj);
        }
    }

    // ── Phase 2 unlock ────────────────────────────────────────────────────────

    private void checkPhase2Unlock() {
        if (phase2Unlocked) return;

        if (bgManager.getPollutionLevel() <= PHASE2_POLLUTION_THRESHOLD) {
            phase2Unlocked = true;
            System.out.println("[FallingObjectSpawner] Phase 2 unlocked! New entities unlocked.");
        }
    }

    public boolean isPhase2Unlocked() {
        return phase2Unlocked;
    }

    // ── Spawn ─────────────────────────────────────────────────────────────────

    private void spawnObject(float screenW, float screenH) {
        float x = random.nextFloat() * (screenW - OBJECT_SIZE);
        float y = screenH + OBJECT_SIZE;
        float fallSpeed = FALL_SPEED;

        boolean spawnGood = random.nextFloat() < GREEN_RATIO;
        SpawnOption[] options = getSpawnTable(spawnGood);
        SpawnOption option = options[random.nextInt(options.length)];

        FallingObject obj;
        if (spawnGood) {
            obj = new GreenSolution(x, y, OBJECT_SIZE, fallSpeed, option.type, option.texturePath);
        } else {
            obj = new PollutionObject(x, y, OBJECT_SIZE, fallSpeed, option.type, option.texturePath);
        }

        activeObjects.add(obj);
        entityManager.addEntity(obj);
    }

    /** Returns the correct spawn table for the current map theme and phase. */
    private SpawnOption[] getSpawnTable(boolean spawnGood) {
        MapTheme theme = bgManager.getTheme();

        if (theme == MapTheme.BEACH) {
            if (spawnGood) return phase2Unlocked ? BEACH_GOOD_P2 : BEACH_GOOD_P1;
            return phase2Unlocked ? BEACH_BAD_P2 : BEACH_BAD_P1;
        }

        // Default: City
        if (spawnGood) return phase2Unlocked ? CITY_GOOD_P2 : CITY_GOOD_P1;
        return phase2Unlocked ? CITY_BAD_P2 : CITY_BAD_P1;
    }

    // ── Effect helpers ────────────────────────────────────────────────────────

    private void applyBadBounceExitTop() {
        scoreManager.addPoints(SCORE_BOUNCE_EXIT_TOP);
    }

    private void applyMissedGround(FallingObject obj) {
        if (obj instanceof GreenSolution) {
            bgManager.decreasePollution(POLLUTION_BOTTOM_GOOD);
            scoreManager.addPoints(SCORE_BOUNCE_EXIT_TOP);
        } else {
            bgManager.increasePollution(POLLUTION_BOTTOM_BAD);
        }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    public void dispose() {
        for (FallingObject obj : activeObjects) {
            entityManager.removeEntity(obj);
        }
        activeObjects.clear();
        upMovement.clear();
        FallingObject.disposeSharedTextures();
        spawnTimer = 0f;
        phase2Unlocked = false;
    }

    // ── Inner class ───────────────────────────────────────────────────────────

    /** Pairs an {@link ObjectType} with its texture asset file name. */
    private static final class SpawnOption {
        private final ObjectType type;
        private final String texturePath;

        private SpawnOption(ObjectType type, String texturePath) {
            this.type = type;
            this.texturePath = texturePath;
        }
    }
}
