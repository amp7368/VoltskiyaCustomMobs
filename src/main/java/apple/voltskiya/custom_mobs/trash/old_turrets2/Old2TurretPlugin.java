package apple.voltskiya.custom_mobs.trash.old_turrets2;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class Old2TurretPlugin extends PluginManagedModule {
    private static Old2TurretPlugin instance;

    public static Old2TurretPlugin get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        Old2TurretList.initialize();
        new Old2TurretCommand();
        new Old2TurretListener();
    }

    @Override
    public String getName() {
        return "turrets";
    }
}
