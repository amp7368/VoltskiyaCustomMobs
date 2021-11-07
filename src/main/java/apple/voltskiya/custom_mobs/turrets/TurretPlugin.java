package apple.voltskiya.custom_mobs.turrets;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class TurretPlugin extends PluginManagedModule {
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
