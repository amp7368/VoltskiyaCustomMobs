package apple.voltskiya.custom_mobs.turret.player;

import apple.voltskiya.custom_mobs.turret.parent.TurretMobConfig;

public class TurretMobPlayerConfig extends TurretMobConfig {
    private static TurretMobPlayerConfig instance;

    public TurretMobPlayerConfig() {
        instance = this;
    }

    public static TurretMobPlayerConfig get() {
        return instance;
    }
}
