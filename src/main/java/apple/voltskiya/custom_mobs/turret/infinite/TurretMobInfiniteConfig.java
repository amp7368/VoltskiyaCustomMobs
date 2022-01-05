package apple.voltskiya.custom_mobs.turret.infinite;

import apple.voltskiya.custom_mobs.turret.parent.TurretMobConfig;

public class TurretMobInfiniteConfig extends TurretMobConfig {
    private static TurretMobInfiniteConfig instance;

    public TurretMobInfiniteConfig() {
        instance = this;
    }

    public static TurretMobInfiniteConfig get() {
        return instance;
    }
}
