package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.DeadRecordedMob;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalMoveToTarget;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.constants.TagConstants;

public class MobReviverBasic extends MobReviver<ReviverConfigBasic> {

    public MobReviverBasic(Entity reviver, ReviverConfigBasic config) {
        super(reviver, config);
    }

    @Override
    protected void doAbility() {
        DeadRecordedMob mobToRevive = ReviveDeadManager.getNearestMob(config.deadTooLong, getLocation());
        if (mobToRevive == null ||
                DistanceUtils.distance(mobToRevive.getLocation(), getLocation()) > config.searchRadius) {
            return;
        }
        Location targetLocation = mobToRevive.getLocation();
        while (targetLocation.getBlock().getType().isAir() && targetLocation.getY() >= 0) {
            targetLocation.subtract(0, 1, 0);
        }
        targetLocation = targetLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        TagConstants.addIsDoingAbility(getBukkitEntity());
        getBukkitEntity().addScoreboardTag(TagConstants.isDoingAbility);
        DecodeEntity.getGoalSelector(getEntityInsentient())
                .a(-1, new PathfinderGoalMoveToTarget(getEntityInsentient(),
                        targetLocation,
                        1.6,
                        config.giveUpTick,
                        () -> reviveStart(mobToRevive)));
    }

    private void reviveStart(DeadRecordedMob reviveMe) {
        if (DistanceUtils.distance(reviveMe.getLocation(), getLocation()) > 2) {
            // say we failed
            ReviveDeadManager.addMob(reviveMe);
            quitRevive();
            return;
        }
        Mob reviver = getBukkitMob();
        reviver.setAI(false);

        final Location reviverLocation = reviver.getLocation();
        final Location reviveMeLocation = reviveMe.getLocation();
        double x = reviveMeLocation.getX() - reviverLocation.getX();
        double z = reviveMeLocation.getZ() - reviverLocation.getZ();
        double magnitude = x * x + z * z;

        Location newLoc = reviverLocation.setDirection(new Vector(x / magnitude, -.5, z / magnitude));
        reviver.teleport(newLoc);

        final World world = reviverLocation.getWorld();
        world.playSound(reviveMe.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 35, .7f);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> doReviveRitual(reviveMe, config.reviveRitualTime), 0);
    }

    private void quitRevive() {
        Mob reviver = getBukkitMob();
        reviver.setAI(true);
        reviver.removeScoreboardTag(TagConstants.isDoingAbility);
    }

    private void doReviveRitual(DeadRecordedMob reviveMe, int time) {
        if (time < 0) {
            quitRevive();
            doReviveSummon(reviveMe);
            return;
        }
        final EntityInsentient handle = this.getEntityInsentient();
        // if the mob was just spawned or it was hurt a while ago
        if ((DecodeEntity.getHurtTimestamp(handle) == 0 ||
                DecodeEntity.getHurtTimestamp(handle) + time <= DecodeEntity.getTicksLived(handle)
        ) && !getBukkitEntity().isDead()) {
            Location reviverLocation = getLocation();
            World world = getWorld();
            double xLoc = reviverLocation.getX();
            double yLoc = reviverLocation.getY();
            double zLoc = reviverLocation.getZ();
            for (int i = 0; i < 15; i++) {
                double xi = random.random().nextDouble() - .5;
                double yi = random.random().nextDouble() * 2;
                double zi = random.random().nextDouble() - .5;
                world.spawnParticle(Particle.REDSTONE, xLoc + xi, yLoc + yi, zLoc + zi, 1, new Particle.DustOptions(Color.RED, 1f));
            }
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> doReviveRitual(reviveMe, time - 3), 3);
        } else {
            reviveMe.resetCooldown(config.deadCooldown);
            quitRevive();
        }
    }
}
