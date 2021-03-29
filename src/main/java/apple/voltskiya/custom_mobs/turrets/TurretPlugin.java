package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class TurretPlugin extends VoltskiyaModule {
    private static TurretPlugin instance;

    @Override
    public void enable() {
        instance = this;
        new TurretManagerTicker();
        new TurretCommand();
    }

    @Override
    public String getName() {
        return "Turret";
    }

    public static TurretPlugin get() {
        return instance;
    }
}
