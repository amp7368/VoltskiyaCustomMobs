package apple.voltskiya.custom_mobs.turrets.mobs;

public enum TargetingMode {
    NONE(0, "None", false, false),
    PLAYERS(1, "Players", true, false),
    MOBS(2, "Mobs", false, true),
    ALL(3, "All", true, true);

    private static TargetingMode[] modes = null;
    private final int index;
    private final String prettyName;
    private final boolean targetsPlayers;
    private final boolean targetsMobs;

    TargetingMode(int index, String prettyName, boolean targetsPlayers, boolean targetsMobs) {
        this.index = index;
        this.prettyName = prettyName;
        this.targetsPlayers = targetsPlayers;
        this.targetsMobs = targetsMobs;
    }

    private static TargetingMode[] getModes() {
        if (modes == null) {
            modes = new TargetingMode[values().length];
            for (TargetingMode mode : values()) {
                modes[mode.index] = mode;
            }
        }
        return modes;
    }

    public TargetingMode cycle() {
        return getModes()[(index + 1) % values().length];
    }

    public String pretty() {
        return prettyName;
    }

    public boolean targetsPlayers() {
        return targetsPlayers;
    }

    public boolean targetsMobs() {
        return targetsMobs;
    }
}
