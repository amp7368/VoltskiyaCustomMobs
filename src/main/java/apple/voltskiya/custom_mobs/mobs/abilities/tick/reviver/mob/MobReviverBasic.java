package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.DeadRecordedMob;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalMoveToTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.loot.LootTables;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.constants.TagConstants;

public class MobReviverBasic extends MobReviver<ReviverConfigBasic> {
    private static final long TIME_TO_RISE = 50;
    private static final double PARTICLE_RADIUS = .5;

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

    private void doReviveSummon(DeadRecordedMob reviveMe) {
        Location reviveMeLocation = reviveMe.getLocation();
        reviveMeLocation.getWorld().spawnEntity(reviveMeLocation, reviveMe.getEntityType(), CreatureSpawnEvent.SpawnReason.CUSTOM,
                newMob -> dealWithSummonedMob(reviveMe, newMob)
        );
    }

    private void dealWithSummonedMob(DeadRecordedMob reviveMe, Entity newMob) {
        Location reviveMeLocation = reviveMe.getLocation();
        NBTTagCompound nbt = reviveMe.getNbt();
        addLinkedMob(newMob);
        final net.minecraft.world.entity.Entity newMobHandle = ((CraftEntity) newMob).getHandle();
        nbt.remove("UUID");
        nbt.remove("DeathTime");
        newMobHandle.load(nbt);
        LivingEntity newMobLiving = (LivingEntity) newMob;
        double health = newMobLiving.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        newMobLiving.setHealth(health);
        if (newMob.getScoreboardTags().contains("was_revived_1")) {
            newMob.addScoreboardTag("was_revived_2");
        } else {
            newMob.addScoreboardTag("was_revived_1");
        }
        newMobLiving.setAI(false);
        newMob.setInvulnerable(true);
        reviveMeLocation.add(0, -3, 0);
        reviveMeLocation.setPitch(-55);
        newMob.teleport(reviveMeLocation);
        double interval = 3d / TIME_TO_RISE;
        for (int time = 0; time < TIME_TO_RISE; time++) {
            if (time % 3 == 0)
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                    getLocation().getWorld().playSound(getLocation(), Sound.BLOCK_GRAVEL_BREAK, 6, 0.75f);
                }, time);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                Location newLocation = newMob.getLocation();
                particles(newLocation);
                newLocation.add(0, interval, 0);
                newMob.teleport(newLocation);
            }, time);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            newMobLiving.setAI(true);
            newMob.setInvulnerable(false);
            ((Mob) newMob).setLootTable(LootTables.EMPTY.getLootTable());
            reviveMe.remove();
        }, TIME_TO_RISE);
    }


    private void particles(Location location) {
        double xi = location.getX();
        double yi = location.getY();
        double zi = location.getZ();
        for (int i = 0; i < 10; i++) {
            double theta = random.random().nextDouble() * 360;
            double radius = random.random().nextDouble() * PARTICLE_RADIUS;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.random().nextDouble() * 2;
            location.getWorld().spawnParticle(Particle.SPELL_WITCH, xi + x, yi + y, zi + z, 1);
        }
    }
}
