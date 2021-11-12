package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.delay_pathfinding.DelayPathfinding;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs.FireFangsManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower.FlameThrowerManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissleShooter;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball.ShootBallManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.charger.ChargerManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.large.LargeOrbitalStrikeManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.small.SmallOrbitalStrikeManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.ReviverManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.warper.WarperManagerTicker;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobTickSpawnListener implements Listener {
    private static final Map<String, RegisteredEntityEater> spawnEater = new HashMap<>();
    private static final Map<String, RegisteredEntityEater> spawnModifier = new HashMap<>();
    private static final Map<String, MobEntityEater<?>> spawnEaterVariants = new HashMap<>();

    public MobTickSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        try {
            List<RegisteredEntityEater> entityEaters = List.of(
                    new LostSoulManagerTicker(),
                    new BlemishSpawnManager(),
                    new LargeOrbitalStrikeManagerTicker(),
                    new WarperManagerTicker(),
                    new ChargerManagerTicker()
            );
            for (RegisteredEntityEater eater : entityEaters) {
                spawnEater.put(eater.getName(), eater);
            }
            spawnEater.put("orbital_striker_small", new SmallOrbitalStrikeManagerTicker());
            spawnEater.put("orbital_striker_large", new LargeOrbitalStrikeManagerTicker());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("There was an issue with one of the config settings of spawnEaters.\n" +
                    "You may have changed a setting that resulted in changing the type of data that was in one of the fields.");
            e.printStackTrace();
        }
        for (RegisteredEntityEater spawnEater : spawnEater.values()) {
            spawnEater.registerInDB();
            spawnEater.eatMobs();
        }
        try {
            new MicroMissileConfig();
            List<RegisteredEntityEater> entityEaters = List.of(
                    new MicroMissleShooter(),
                    new FireFangsManager(),
                    new FlameThrowerManager(),
                    new DelayPathfinding(),
                    new ShootBallManager()
            );
            for (RegisteredEntityEater eater : entityEaters) {
                spawnModifier.put(eater.getName(), eater);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("There was an issue with one of the config settings of spawnModifiers.\n" +
                    "You may have changed a setting that resulted in changing the type of data that was in one of the fields.");
            e.printStackTrace();
        }
        for (RegisteredEntityEater spawnEater : spawnModifier.values()) {
            spawnEater.registerInDB();
            spawnEater.eatMobs();
        }
        Collection<MobEntityEater<?>> entityEaters = List.of(
                new ReviverManager()
        );
        for (MobEntityEater<?> spawnEater : entityEaters) {
            for (String tag : spawnEater.getTags()) {
                spawnEaterVariants.put(tag, spawnEater);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            RegisteredEntityEater eater = spawnEater.get(tag);
            if (eater != null) eater.eatAndRegisterEvent(event);
            RegisteredEntityEater modifier = spawnModifier.get(tag);
            if (modifier != null) modifier.eatAndRegisterEvent(event);
            MobEntityEater<?> entityEater = spawnEaterVariants.get(tag);
            if (entityEater != null) entityEater.eatEvent(tag, event);
        }
    }
}
