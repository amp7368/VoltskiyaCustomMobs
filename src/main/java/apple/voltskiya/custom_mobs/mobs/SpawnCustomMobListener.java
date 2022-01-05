package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.MobAledarEntityEater;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobInfected;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.register.MobAPC33EntityEater;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntityEater;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
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

    public SpawnCustomMobListener(List<NmsSpawnWrapper<?>> spawners) {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        spawnEaters.put(MobInfected.PARASITE_INFECTED_TAG, MobInfected::spawnEat);


        for (NmsSpawnWrapper<?> spawner : spawners) {
            spawnEaters.put(spawner.tag(), spawner::spawnEat);
        }

        for (NmsMobEntityEater<?> register : nmsMobRegister) {
            for (String tag : register.getTags()) {
                nmsEntityEaters.put(tag, register);
            }
        }

    }

    public static void initialize() {
        nmsMobRegister = List.of(new MobAPC33EntityEater(), new MobAledarEntityEater());
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
