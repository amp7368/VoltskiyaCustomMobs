package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.large;

public class OrbitalStrikeConfig {

    private static OrbitalStrikeConfig instance;
    public OrbitalStrikeTypeConfig large = new OrbitalStrikeTypeConfig();
    public OrbitalStrikeTypeConfig small = new OrbitalStrikeTypeConfig();
    public OrbitalStrikeTypeConfig medium = new OrbitalStrikeTypeConfig();

    public OrbitalStrikeConfig() {
        instance = this;
    }

    public static OrbitalStrikeConfig get() {
        return instance;
    }

    public static class OrbitalStrikeTypeConfig {

        public double chancePerTickADouble = 0.01;
        public int cooldown = 800;
        public int targetingRange = 100;
        public double radius = 6;
        public double minHeight = 20;
        public double height = 20;
        public int totalTime = 300;
        public int targetTime = 30;
        public double shootInterval = 1;
        public double movementSpeed = 2.2;
        public int movementTargetLag = 2;
    }
}
