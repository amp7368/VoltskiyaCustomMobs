package apple.voltskiya.custom_mobs.mobs.abilities.tick.charger;

public class ChargerConfig {

    private static ChargerConfig instance;
    public ChargerTypeConfig normal = new ChargerTypeConfig();
    public ChargerTypeConfig quick = new ChargerTypeConfig();

    public ChargerConfig() {
        instance = this;
    }

    public static ChargerConfig get() {
        return instance;
    }

    public static class ChargerTypeConfig {

        public int overshootDistance = 10;
        public double overshootSpeed = 2;
        public double tooCloseToCharge = 4;
        public double marginOfError = 2.5;
        public int maxChargeTime = 20 * 5;
        public double chargeChance = 0.02;
        public int chargeCooldown = 90;
        public int chargeUpTime = 20;
        public int chargeStunTime = 100;
        public int chargeTiredTime = 50;

    }
}
