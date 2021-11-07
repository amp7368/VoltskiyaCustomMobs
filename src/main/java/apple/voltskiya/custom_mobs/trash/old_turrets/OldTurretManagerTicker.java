package apple.voltskiya.custom_mobs.trash.old_turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.NormalHighFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.trash.old_turrets.gui.OldTurretGuiManager;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.EntityLocation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static apple.voltskiya.custom_mobs.turrets.mobs.TurretMob.TURRET_TAG;

public class OldTurretManagerTicker implements Listener {
    private static OldTurretManagerTicker instance;
    private final HashMap<Long, OldTurretMob> turrets = new HashMap<>();
    private final HashMap<UUID, Long> entityToTurret = new HashMap<>();
    private final Map<Closeness, OldTurretIndividualTicker> closenessToTurrets = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new OldTurretIndividualTicker(closeness));
    }};

    public OldTurretManagerTicker() {
        instance = this;
        try {
            for (OldTurretMob turretMob : TurretsSql.getTurrets()) {
                addTurret(turretMob);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    public static OldTurretManagerTicker get() {
        return instance;
    }

    public synchronized void addTurret(OldTurretMob turretMob) {
        final long uniqueId = turretMob.getUniqueId();
        this.turrets.putIfAbsent(uniqueId, turretMob);
        for (EntityLocation entity : turretMob.getTurretEntities()) {
            entityToTurret.putIfAbsent(entity.uuid, uniqueId);
        }
        closenessToTurrets.get(determineConcern(turretMob)).giveTurret(turretMob);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity.getScoreboardTags().contains(TURRET_TAG)) {
            @Nullable Long uid = entityToTurret.get(entity.getUniqueId());
            if (uid != null) {
                @Nullable OldTurretMob turret = turrets.get(uid);
                if (turret != null) {
                    turret.damage(event.getDamage());
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (entity.getScoreboardTags().contains(TURRET_TAG)) {
            @Nullable Long uid = entityToTurret.get(entity.getUniqueId());
            if (uid != null) {
                @Nullable OldTurretMob turret = turrets.get(uid);
                if (turret != null) {
                    OldTurretGuiManager.get().open(event.getPlayer(), turret);
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean amIGivingTurret(OldTurretMob turret, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(turret);
        if (actualCloseness != currentCloseness) {
            closenessToTurrets.get(actualCloseness).giveTurret(turret);
            return true;
        }
        return false;
    }


    private Closeness determineConcern(OldTurretMob turret) {
        Location turretLocation = turret.getCenter();
        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(turretLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(turretLocation, player.getLocation());
    }

    public void removeTurret(long uid, List<EntityLocation> turretEntities) {
        this.turrets.remove(uid);
        for (EntityLocation e : turretEntities) {
            Entity entity = Bukkit.getEntity(e.uuid);
            if (entity != null) entity.remove();
            this.entityToTurret.remove(e.uuid);
        }
    }

    enum Closeness {
        HIGH_CLOSE(100, NormalHighFrequencyTick.get()),
        NORMAL_CLOSE(150, NormalFrequencyTick.get()),
        LOW_CLOSE(160, LowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE, NORMAL_CLOSE, LOW_CLOSE};
        private final TickGiverable giver;

        Closeness(double distance, TickGiverable giver) {
            this.distance = distance;
            this.giver = giver;
        }

        private static Closeness getCloseness(Location aLocation, Location bLocation) {
            double d = DistanceUtils.distance(aLocation, bLocation);
            for (Closeness closeness : order) {
                if (d <= closeness.distance) {
                    return closeness;
                }
            }
            return lowest();
        }

        public static Closeness lowest() {
            return order[order.length - 1];
        }

        public TickGiverable getGiver() {
            return giver;
        }
    }

}
