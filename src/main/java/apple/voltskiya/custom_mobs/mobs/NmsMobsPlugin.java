package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class NmsMobsPlugin extends VoltskiyaModule {
    private static NmsMobsPlugin instance;

    public static NmsMobsPlugin get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        MobWarpedGremlin.initialize();
        new WarpedGremlinSpawnCommand();
    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
