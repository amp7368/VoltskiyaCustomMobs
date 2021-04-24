package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.testing.MobEndermanVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;

import java.io.File;

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
        MobEndermanVex.initialize();
        MobEyePlant.initialize();
        new MobsSpawnCommand();
    }

    @Override
    public String getName() {
        return "Mobs";
    }

    public File getModelDataFolder() {
        final File folder = new File(getDataFolder(), "models");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }
}
