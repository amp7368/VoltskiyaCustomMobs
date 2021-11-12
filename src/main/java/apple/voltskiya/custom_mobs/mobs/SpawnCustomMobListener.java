package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.modified.illager.evoker.MobIllagerEvokerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator.MobIllagerVindicatorExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobTestSkeleton;
import apple.voltskiya.custom_mobs.mobs.nms.nether.angered_soul.MobAngeredSoul;
import apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nms.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobInfected;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nms.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.register.MobAPC33EntityEater;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntityEater;
import apple.voltskiya.custom_mobs.trash.aledar.MobCart;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnCustomMobListener implements Listener {
    public static final String CUSTOM_SPAWN_COMPLETE_TAG = "CUSTOM_SPAWN_COMPLETE";
    private final Map<String, CustomSpawnEater> spawnEaters = new HashMap<>();
    private static List<NmsMobEntityEater<?>> nmsMobRegister;
    private final Map<String, NmsMobEntityEater<?>> nmsEntityEaters = new HashMap<>();

    public SpawnCustomMobListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        spawnEaters.put(MobEyePlant.REGISTERED_NAME, MobEyePlant::spawnEat);
        spawnEaters.put(MobWarpedGremlin.REGISTERED_NAME, MobWarpedGremlin::spawnEat);
        spawnEaters.put(MobParasite.REGISTERED_NAME, MobParasite::spawnEat);
        spawnEaters.put(MobCart.REGISTERED_NAME, MobCart::spawnEat);
        spawnEaters.put(MobIronGolemExaminer.REGISTERED_NAME, MobIronGolemExaminer::spawnEat);
        spawnEaters.put(MobIllagerIllusionerExaminer.REGISTERED_NAME, MobIllagerIllusionerExaminer::spawnEat);
        spawnEaters.put(MobIllagerPillagerExaminer.REGISTERED_NAME, MobIllagerPillagerExaminer::spawnEat);
        spawnEaters.put(MobIllagerEvokerExaminer.REGISTERED_NAME, MobIllagerEvokerExaminer::spawnEat);
        spawnEaters.put(MobIllagerVindicatorExaminer.REGISTERED_NAME, MobIllagerVindicatorExaminer::spawnEat);
        spawnEaters.put(MobInfected.PARASITE_INFECTED_TAG, MobInfected::spawnEat);
        spawnEaters.put(MobRevenant.REGISTERED_NAME, MobRevenant::spawnEat);
        spawnEaters.put(MobAngeredSoul.REGISTERED_NAME, MobAngeredSoul::spawnEat);
        spawnEaters.put(MobHealthPack.REGISTERED_NAME, MobHealthPack::spawnEat);
        spawnEaters.put(MobTestSkeleton.REGISTERED_NAME, MobTestSkeleton::spawnEat);

        for (NmsMobEntityEater<?> register : nmsMobRegister) {
            for (String tag : register.getTags()) {
                nmsEntityEaters.put(tag, register);
            }
        }
    }

    public static void initialize() {
        nmsMobRegister = List.of(new MobAPC33EntityEater());
    }

    @EventHandler(ignoreCancelled = true)
    public void spawnCustom(CreatureSpawnEvent event) {
        // if the entity wasn't already custom spawned
        if (!event.getEntity().getScoreboardTags().contains(CUSTOM_SPAWN_COMPLETE_TAG)) {
            for (String tag : event.getEntity().getScoreboardTags()) {
                CustomSpawnEater eater = spawnEaters.get(tag);
                if (eater != null) {
                    eater.eatSpawnEvent(event);
                    break;
                }
                NmsMobEntityEater<?> nmsEater = nmsEntityEaters.get(tag);
                if (nmsEater != null) {
                    nmsEater.eatSpawnEvent(tag, event);
                    break;
                }
            }
        }
    }

    public interface CustomSpawnEater {
        void eatSpawnEvent(CreatureSpawnEvent event);
    }
}
