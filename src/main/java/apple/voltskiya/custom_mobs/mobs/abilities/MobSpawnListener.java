package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
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
import apple.voltskiya.custom_mobs.mobs.abilities.tick.revive.ReviverManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.warper.WarperManagerTicker;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MobSpawnListener implements Listener {
    private static final Map<String, RegisteredEntityEater> spawnEater = new HashMap<>();
    private static final Map<String, RegisteredEntityEater> spawnModifier = new HashMap<>();

    public MobSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        try {
            spawnEater.put("lost_soul", new LostSoulManagerTicker());
            spawnEater.put(BlemishSpawnManager.NAME, new BlemishSpawnManager());
            spawnEater.put("orbital_striker", new LargeOrbitalStrikeManagerTicker());
            spawnEater.put("orbital_striker_small", new SmallOrbitalStrikeManagerTicker());
            spawnEater.put("warper", new WarperManagerTicker());
            spawnEater.put("reviver", new ReviverManagerTicker());
            spawnEater.put("charger", new ChargerManagerTicker());
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
            spawnModifier.put("micro_missile_shooter", new MicroMissleShooter());
            spawnModifier.put("fire_fangs", new FireFangsManager());
            spawnModifier.put("flamethrower", new FlameThrowerManager());
            spawnModifier.put("guard_stare", new DelayPathfinding());
            spawnModifier.put("shoot_ball", new ShootBallManager());
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
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            RegisteredEntityEater eater = spawnEater.get(tag);
            if (eater != null) eater.eatAndRegisterEvent(event);
            RegisteredEntityEater modifier = spawnModifier.get(tag);
            if (modifier != null) modifier.eatAndRegisterEvent(event);
        }
    }
}
