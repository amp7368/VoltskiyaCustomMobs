package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class MicroMissileIndividualTicker implements Tickable, Listener {
    public static final String MICRO_MISSLE_TAG = "micro_missle";
    private static MicroMissileIndividualTicker instance;
    private final TickGiverable giver;
    private final ArrayList<UUID> vexes = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid = -1;
    private final long callerUid = UpdatedPlayerList.callerUid();


    public MicroMissileIndividualTicker() {
        instance = this;
        this.giver = HighFrequencyTick.get();
    }

    public MicroMissileIndividualTicker get() {
        return instance;
    }

    @Override
    public synchronized void tick() {
        Iterator<UUID> vexUidIterator = vexes.iterator();
        boolean trim = false;
        while (vexUidIterator.hasNext()) {
            UUID vexUid = vexUidIterator.next();
            Entity entity = Bukkit.getEntity(vexUid);
            if (entity == null || entity.getType() != EntityType.VEX) {
                // remove this vex D:
                vexUidIterator.remove();
                trim = true;
            } else {
                Vex vex = (Vex) entity;
                this.tickVex(vex);
            }
        }
        if (trim) {
            vexes.trimToSize();
            if (isTicking && vexes.isEmpty()) {
                giver.remove(myTickerUid);
                isTicking = false;
            }
        }
    }

    private synchronized void tickVex(Vex vex) {
        final Location vexLocation = vex.getLocation();
        this.particles(vexLocation);

        if (vexLocation.getBlock().getType().isSolid()) {
            Collection<Entity> nearbyEntities = vexLocation.getNearbyEntities(1, 1, 1);
            for (Entity nearby : nearbyEntities) {
                if (nearby instanceof LivingEntity) {
                    vexLocation.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, vexLocation, 0);
                    vexLocation.getWorld().playSound(vexLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    ((LivingEntity) nearby).damage(MicroMissleSpawnManager.get().DAMAGE_AMOUNT);
                    MobListSql.removeMob(vex.getUniqueId());
                    vex.remove();
                }
            }
        } else {
            Player player = UpdatedPlayerList.getCollision(vex.getBoundingBox(), callerUid);
            if (player != null) {
                vexLocation.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, vexLocation, 0);
                vexLocation.getWorld().playSound(vexLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                player.damage(MicroMissleSpawnManager.get().DAMAGE_AMOUNT, vex);
                MobListSql.removeMob(vex.getUniqueId());
                vex.remove();
            }
        }
    }

    private void particles(Location location) {
        location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.01, 0.01, 0.01, 0.014);
        location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 10, 0.04, 0.04, 0.04, 0.02);
    }

    public synchronized void giveVex(Vex vex) {
        this.vexes.add(vex.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = giver.add(this::tick);
        }
    }
}