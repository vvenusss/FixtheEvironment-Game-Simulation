package io.github.some_example_name.lwjgl3;

/**
 * ObjectType — Enumerates every type of falling object in FutureX.
 *
 * <h3>Categories</h3>
 * <ul>
 *   <li><b>Pollution objects</b> ({@link #FACTORY}, {@link #CAR},
 *       {@link #TRASH}, {@link #BATTERY}) — increase pollution when treated as bad.</li>
 *   <li><b>Green solutions</b> ({@link #TREE}, {@link #SOLAR_PANEL},
 *       {@link #RECYCLING_BIN}, {@link #WOOD}) — reduce pollution when treated as good.</li>
 * </ul>
 */
public enum ObjectType {

    // ── Pollution objects ─────────────────────────────────────────────────────
    FACTORY("Factory", true),
    CAR("Car", true),
    TRASH("Trash", true),
    BATTERY("Battery", true),

    // ── Green solutions ───────────────────────────────────────────────────────
    TREE("Tree", false),
    SOLAR_PANEL("Solar Panel", false),
    RECYCLING_BIN("Recycling Bin", false),
    WOOD("Wood", false);

    private final String label;
    private final boolean pollution;

    ObjectType(String label, boolean pollution) {
        this.label = label;
        this.pollution = pollution;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPollution() {
        return pollution;
    }

    public boolean isGreenSolution() {
        return !pollution;
    }
}