package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.modified.illager.evoker.MobIllagerEvokerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator.MobIllagerVindicatorExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobCart;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class SpawnCustomMobListener implements Listener {
    public static final String CUSTOM_SPAWN_COMPLETE_TAG = "CUSTOM_SPAWN_COMPLETE";
    private final Map<String, CustomSpawnEater> spawnEaters = new HashMap<>();

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
            }
        }
    }

    private interface CustomSpawnEater {
        void eatSpawnEvent(CreatureSpawnEvent event);
    }
}
