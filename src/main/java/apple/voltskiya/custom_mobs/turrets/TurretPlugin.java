package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class TurretPlugin extends VoltskiyaModule {
    @Override
    public void enable() {
        new TurretManagerTicker();
        new TurretCommand();
    }

    @Override
    public String getName() {
        return "Turret";
    }
}
