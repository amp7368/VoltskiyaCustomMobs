package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.mobs.modified.illager.evoker.MobIllagerEvokerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator.MobIllagerVindicatorExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.MobCart;
import apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.mob.AledarNavigation;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.mobs.nms.nether.angered_soul.MobAngeredSoul;
import apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nms.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartArmorStand;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.ConfigBuilderHolder;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PluginNmsMobs extends PluginManagedModule implements PluginManagedModuleConfig {
    private static PluginNmsMobs instance;
    private List<NmsSpawnWrapper<?>> spawners = new ArrayList<>();

    @Override
    public void enable() {
        new AledarNavigation();
        new SpawnCustomMobListener(spawners);
    }

    @Override
    public String getName() {
        return "NmsMobs";
    }

    public static PluginNmsMobs get() {
        return instance;
    }

    public File getModelDataFolder() {
        final File folder = new File(getDataFolder(), "models");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    @Override
    public void init() {
        instance = this;
        new NmsModelHandler();
        spawners = List.of(
                MobIllagerEvokerExaminer.spawner(),
                MobIronGolemExaminer.spawner(),
                MobIllagerVindicatorExaminer.spawner(),
                MobIllagerPillagerExaminer.spawner(),
                MobIllagerIllusionerExaminer.spawner(),
                MobPartArmorStand.spawner(),
                MobEyePlant.spawner(),
                MobAngeredSoul.spawner(),
                MobHealthPack.spawner(),
                MobParasite.spawner(),
                MobCart.spawner(),
                MobRevenant.spawner()
        );
        for (NmsSpawnWrapper<?> spawner : spawners) {
            spawner.initialize();
        }

        SpawnCustomMobListener.initialize();
    }

    @Override
    public Collection<ConfigBuilderHolder<?>> getConfigsToRegister() {
        return Collections.emptyList();
    }
}
