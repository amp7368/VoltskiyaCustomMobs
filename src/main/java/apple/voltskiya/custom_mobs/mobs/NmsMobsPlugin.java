package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.testing.MobEndermanVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.AledarNavigation;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobCart;

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
        MobParasite.initialize();
        MobCart.initialize();
        MobIronGolemExaminer.initialize();
        MobIllagerIllusionerExaminer.initialize();
        MobIllagerPillagerExaminer.initialize();
        MobRevenant.initialize();
        new AledarNavigation();
        new MobsSpawnCommand();
        new SpawnCustomMobListener();
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
