package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

public class FlameThrowerConfig {

    private static FlameThrowerConfig instance;
    public double minRange = 13;
    public double range = 4;
    public int cooldown = 300;

    public FlameThrowerConfig() {
        instance = this;
    }

    public static FlameThrowerConfig get() {
        return instance;
    }
}
