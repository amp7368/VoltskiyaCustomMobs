package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball;

public class ShootBallConfig {

    private static ShootBallConfig instance;
    public ShootBallTypeConfig normal = new ShootBallTypeConfig();
    public ShootBallTypeConfig overseer = new ShootBallTypeConfig();

    public ShootBallConfig() {
        instance = this;
    }

    public static ShootBallConfig get() {
        return instance;
    }

    public static class ShootBallTypeConfig {

        public double range = 50;
        public double step = 1;
        public int cooldown = 300;
    }
}
