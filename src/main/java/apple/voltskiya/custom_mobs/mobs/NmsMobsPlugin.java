package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.testing.EndermanVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;

public class NmsMobsPlugin extends VoltskiyaModule {
    private static NmsMobsPlugin instance;

    public static NmsMobsPlugin get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        NmsModelConfig.initialize();
        MobZombieCow.initialize();
        MobWarpedGremlin.initialize();
        MobPartArmorStand.initialize();
        MobPiglinVex.initialize();
        MobAledar.initialize();
        EndermanVex.initialize();
        new MobsSpawnCommand();
    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
