package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileShooter;
import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.IllegalPluginAccessException;

public class PathfinderGoalShootMicroMissle extends Goal {

    public static final double SHOOT_FREQUENCY = .05;
    private final Random random = new Random();
    private final Mob me;
    private final int cooldown;
    private final int count;
    private final MicroMissileShooter.MissileType missileType;
    private int lastShot = 0;

    public PathfinderGoalShootMicroMissle(Mob me, int cooldown, int count,
        MicroMissileShooter.MissileType missileType) {
        this.me = me;
        this.cooldown = cooldown;
        this.count = count;
        this.missileType = missileType;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean canUse() {
        boolean successChanced = random.nextFloat() < SHOOT_FREQUENCY;
        boolean hasTarget = this.me.getTarget() != null;
        boolean recentlyHit = me.tickCount - this.lastShot >= cooldown;
        return successChanced && hasTarget && recentlyHit;
    }

    @Override
    public void tick() {
        final Location targetLocation = getTargetLocation();
        if (targetLocation != null) {
            this.lastShot = me.tickCount;
            Location shootFromLocation = ((LivingEntity) this.me.getBukkitEntity()).getEyeLocation();
            if (missileType == MicroMissileShooter.MissileType.FLURRY) {
                sounds();
                try {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                        MicroMissileManager.shoot(shootFromLocation, targetLocation, count,
                            missileType);
                    }, 24);
                } catch (IllegalPluginAccessException ignored) {
                    // doesn't matter if the action is interuppted
                }
            } else {
                singleSound();
                MicroMissileManager.shoot(shootFromLocation, targetLocation, count, missileType);
            }
        }
    }

    private void singleSound() {
        final Location location = me.getBukkitEntity().getLocation();
        location.getWorld()
            .playSound(location, Sound.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 0.3f, 2f);
    }

    private void sounds() {
        final Location location = me.getBukkitEntity().getLocation();
        location.getWorld()
            .playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2f, 1.5f);
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld()
                    .playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.25f,
                        1.6f);
            }, 6);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld()
                    .playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.5f,
                        1.7f);
            }, 12);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld()
                    .playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.75f,
                        1.8f);
            }, 18);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld()
                    .playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 3f,
                        1.9f);
            }, 24);
        } catch (IllegalPluginAccessException ignored) {
            // doesn't matter if the action is interuppted
        }
    }

    @Nullable
    private Location getTargetLocation() {
        final net.minecraft.world.entity.LivingEntity goalTarget = this.me.getTarget();
        if (goalTarget == null)
            return null;
        return ((LivingEntity) goalTarget.getBukkitEntity()).getEyeLocation();
    }
}
