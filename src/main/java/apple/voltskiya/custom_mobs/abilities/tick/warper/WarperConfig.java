package apple.voltskiya.custom_mobs.abilities.tick.warper;

public class WarperConfig {

    public int PARTICLES;
    public int WARP_RADIUS;
    public double WARP_CHANCE;
    private static WarperConfig instance;

    public WarperConfig() {
        instance = this;
    }

    public static WarperConfig get() {
        return instance;
    }
}
