package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs;

public class FireFangsConfig {

    private static FireFangsConfig instance;
    public FireFangsTypeConfig normal = new FireFangsTypeConfig(15, 1, 300);
    public FireFangsTypeConfig triple = new FireFangsTypeConfig(15, 1, 300);
    public FireFangsTypeConfig tripleStraight = new FireFangsTypeConfig(15, 1, 300);
    public FireFangsTypeConfig blueNormal = new FireFangsTypeConfig(15, 1, 300);
    public FireFangsTypeConfig blueTriple = new FireFangsTypeConfig(15, 1, 300);
    public FireFangsTypeConfig blueTripleStraight = new FireFangsTypeConfig(15, 1, 300);

    public FireFangsConfig() {
        instance = this;
    }

    public static FireFangsConfig get() {
        return instance;
    }

    public static class FireFangsTypeConfig {

        public double range;
        public double step;
        public int cooldown;

        public FireFangsTypeConfig(double range, double step, int cooldown) {
            this.range = range;
            this.step = step;
            this.cooldown = cooldown;
        }
    }
}
