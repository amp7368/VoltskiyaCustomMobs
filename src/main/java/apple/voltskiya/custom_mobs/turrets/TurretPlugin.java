package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class TurretPlugin extends VoltskiyaModule {
    private static TurretPlugin instance;

    public static TurretPlugin get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        TurretList.initialize();
        new TurretCommand();
        new TurretListener();
    }

    @Override
    public String getName() {
        return "Turrets";
    }
}
