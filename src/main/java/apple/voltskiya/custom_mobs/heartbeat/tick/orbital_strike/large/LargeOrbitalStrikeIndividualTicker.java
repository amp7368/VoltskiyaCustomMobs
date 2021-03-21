package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.large;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.Pair;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrike;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;


public class LargeOrbitalStrikeIndividualTicker {
    private final LargeOrbitalStrikeManagerTicker.Closeness closeness;
    private boolean isCheckStrike = false;
    private final ArrayList<Pair<UUID, Long>> strikers = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid;
    private final Random random = new Random();
    private final long callerUid = UpdatedPlayerList.callerUid();


    public LargeOrbitalStrikeIndividualTicker(LargeOrbitalStrikeManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveStriker(Entity striker, long lastShot) {
        this.strikers.add(new Pair<>(striker.getUniqueId(), lastShot));
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    public synchronized void tick() {
        Iterator<Pair<UUID, Long>> strikerUidIterator = strikers.iterator();
        boolean trim = false;
        long now = System.currentTimeMillis();
        while (strikerUidIterator.hasNext()) {
            Pair<UUID, Long> strikerUid = strikerUidIterator.next();
            if (now - strikerUid.getValue() < LargeOrbitalStrikeManagerTicker.get().STRIKE_COOLDOWN) {
                continue;
            }
            Entity striker = Bukkit.getEntity(strikerUid.getKey());
            if (striker == null) {
                // remove this striker D:
                MobListSql.removeMob(strikerUid.getKey());
                strikerUidIterator.remove();
                trim = true;
            } else {
                tickStriker(striker, strikerUid);
                if (LargeOrbitalStrikeManagerTicker.get().amIGivingStriker(striker, closeness, strikerUid.getValue())) {
                    MobListSql.removeMob(strikerUid.getKey());
                    strikerUidIterator.remove();
                    trim = true;
                }
            }
        }
        if (trim) {
            if (isTicking && strikers.isEmpty()) {
                closeness.getGiver().remove(myTickerUid);
                isTicking = false;
            }
            strikers.trimToSize();
        }
    }

    private synchronized void tickStriker(Entity striker, Pair<UUID, Long> strikerUid) {
        if (isCheckStrike) {
            if (random.nextDouble() < LargeOrbitalStrikeManagerTicker.get().STRIKE_CHANCE * closeness.getGiver().getTickSpeed()) {
                checkStrike(striker, strikerUid);
            }
        }
    }

    private synchronized void checkStrike(Entity striker, Pair<UUID, Long> strikerUid) {
        Location strikerLocation = striker.getLocation();
        @Nullable LivingEntity target = ((Mob) striker).getTarget();
        if (target == null) {
            Player closest = UpdatedPlayerList.getClosestPlayer(striker.getLocation(), callerUid);
            if (closest != null) {
                Location pLocation = closest.getLocation();
                double d = DistanceUtils.distance(pLocation, strikerLocation);
                if (d < LargeOrbitalStrikeManagerTicker.get().STRIKE_DISTANCE && ((Mob) striker).hasLineOfSight(closest)) {
                    target = closest;
                }
            }
        }
        if (target != null) {
            // we have the target. time to orbital strike it
            final Location targetLocation = target.getLocation();
            ((Mob) striker).setAI(false);
            ((Mob) striker).setTarget(null);
            new OrbitalStrike(target.getWorld(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), OrbitalStrike.OrbitalStrikeType.LARGE, callerUid);
            strikerUid.setValue(System.currentTimeMillis());
            @Nullable LivingEntity finalTarget = target;
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                ((Mob) striker).setAI(true);
                ((Mob) striker).setTarget(finalTarget);
            }, LargeOrbitalStrikeManagerTicker.get().STRIKE_TIME);
        }
    }

    public synchronized void setIsCheckStrike() {
        this.isCheckStrike = true;
    }

}
