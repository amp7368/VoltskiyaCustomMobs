package apple.voltskiya.custom_mobs.abilities.tick.reviver.dead;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.abilities.tick.DeathEater;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

public class ReviveDeadManager implements DeathEater {

    private static final List<DeadRecordedMob> deadMobs = new ArrayList<>();

    public static DeadRecordedMob getNearestMob(int deadTooLong, Location location) {
        DeadRecordedMob closest = null;
        double distance = Double.MAX_VALUE;
        synchronized (deadMobs) {
            deadMobs.removeIf(DeadRecordedMob::isDeadTooLong);
            for (DeadRecordedMob mob : deadMobs) {
                if (mob.isCooldownUp() && !mob.isDeadTooLong(deadTooLong)) {
                    double d = VectorUtils.distance(location, mob.getLocation());
                    if (d < distance) {
                        distance = d;
                        closest = mob;
                    }
                }
            }
        }
        return closest;
    }

    public static List<DeadRecordedMob> removeMobsInRadius(int deadTooLong, Location location,
        double distance) {
        List<DeadRecordedMob> mobs = new ArrayList<>();
        synchronized (deadMobs) {
            deadMobs.removeIf(DeadRecordedMob::isDeadTooLong);
            for (Iterator<DeadRecordedMob> iterator = deadMobs.iterator(); iterator.hasNext(); ) {
                DeadRecordedMob mob = iterator.next();
                if (mob.isCooldownUp() && !mob.isDeadTooLong(deadTooLong)) {
                    double d = VectorUtils.distance(location, mob.getLocation());
                    if (d < distance) {
                        mobs.add(mob);
                        iterator.remove();
                    }
                }
            }
        }
        return mobs;
    }

    public static void addMob(DeadRecordedMob mob) {
        synchronized (deadMobs) {
            deadMobs.add(mob);
        }
    }

    public static void removeMob(DeadRecordedMob dead) {
        synchronized (deadMobs) {
            deadMobs.remove(dead);
        }
    }

    @Override
    public void eatEvent(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        synchronized (deadMobs) {
            deadMobs.add(new DeadRecordedMob(entity));
        }
    }
}
