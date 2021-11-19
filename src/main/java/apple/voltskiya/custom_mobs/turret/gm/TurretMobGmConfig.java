package apple.voltskiya.custom_mobs.turret.gm;

import apple.voltskiya.custom_mobs.turret.parent.TurretMobConfig;

public class TurretMobGmConfig extends TurretMobConfig {
    private static TurretMobGmConfig instance;

    public TurretMobGmConfig() {
        instance = this;
    }

    public static TurretMobGmConfig get() {
        return instance;
    }
}
