package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.voltskiya.custom_mobs.PluginDisable;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissleShooter;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.IllegalPluginAccessException;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalShootMicroMissle extends PathfinderGoal {
    public static final double SHOOT_FREQUENCY = .05;
    private final Random random = new Random();
    private final EntityInsentient me;
    private final int cooldown;
    private final int count;
    private int lastShot = 0;
    private final MicroMissleShooter.MissileType missileType;

    public PathfinderGoalShootMicroMissle(EntityInsentient me, int cooldown, int count, MicroMissleShooter.MissileType missileType) {
        this.me = me;
        this.cooldown = cooldown;
        this.count = count;
        this.missileType = missileType;
        this.a(EnumSet.of(Type.TARGET));
        PluginDisable.addMob(me,this);
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        return random.nextFloat() < SHOOT_FREQUENCY && this.me.getGoalTarget() != null && this.me.ticksLived - this.lastShot >= cooldown;
    }

    @Override
    public void c() {
        final Location targetLocation = getTargetLocation();
        if (targetLocation != null) {
            this.lastShot = this.me.ticksLived;
            if (missileType == MicroMissleShooter.MissileType.FLURRY) {
                sounds();
                try {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                        MicroMissileManager.shoot(this.me.getBukkitEntity().getLocation().add(0, this.me.getHeadHeight(), 0), targetLocation, count, missileType);
                    }, 24);
                } catch (IllegalPluginAccessException ignored) {
                    // doesn't matter if the action is interuppted
                }
            } else {
                singleSound();
                MicroMissileManager.shoot(this.me.getBukkitEntity().getLocation().add(0, this.me.getHeadHeight(), 0), targetLocation, count, missileType);
            }
        }
    }

    private void singleSound() {
        final Location location = me.getBukkitEntity().getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 0.3f, 2f);
    }

    private void sounds() {
        final Location location = me.getBukkitEntity().getLocation();
        location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2f, 1.5f);
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.25f, 1.6f);
            }, 6);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.5f, 1.7f);
            }, 12);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.75f, 1.8f);
            }, 18);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 3f, 1.9f);
            }, 24);
        } catch (IllegalPluginAccessException ignored) {
            // doesn't matter if the action is interuppted
        }
    }

    @Nullable
    private Location getTargetLocation() {
        final EntityLiving goalTarget = this.me.getGoalTarget();
        return goalTarget == null ? null : goalTarget.getBukkitEntity().getLocation().add(0, goalTarget.getHeadHeight(), 0);
    }
}
