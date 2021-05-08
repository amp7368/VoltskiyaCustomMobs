package apple.voltskiya.custom_mobs.mobs.nether.angered_soul;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class AngeredSoulScream implements Runnable {
    private static final double VELOCITY = .6;
    private final MobAngeredSoul me;
    private final Entity bukkitMe;
    private Vector velocity = null;
    private int soundTick = 0;
    private int velocityTick = 0;
    private boolean ran = false;

    public AngeredSoulScream(MobAngeredSoul me) {
        this.me = me;
        this.bukkitMe = me.getBukkitEntity();
    }

    @Override
    public void run() {
        if (this.ran) return;
        this.ran = true;
        @Nullable EntityLiving targetEntity = this.me.getGoalTarget();
        final Location myLocation = this.me.getBukkitEntity().getLocation();
        this.velocity = (targetEntity == null) ?
                myLocation.getDirection() :
                targetEntity.getBukkitEntity().getLocation().toVector().subtract(myLocation.toVector()).normalize().multiply(VELOCITY);
        this.firstSound();
        for (int i = 0; i < 27; i += 3) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::face, i);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::postRun, 27);
    }

    private void postRun() {
        @Nullable EntityLiving targetEntity = this.me.getGoalTarget();
        final Location myLocation = this.me.getBukkitEntity().getLocation();
        this.velocity = (targetEntity == null) ?
                myLocation.getDirection() :
                targetEntity.getBukkitEntity().getLocation().toVector().subtract(myLocation.toVector()).normalize().multiply(VELOCITY);
        face();
        this.sound();
        this.velocity();
    }

    private void face() {
        this.bukkitMe.teleport(this.bukkitMe.getLocation().setDirection(velocity));
    }

    private void velocity() {
        if (this.me.isAlive()) {
            if (velocityTick++ == 60) {
                this.me.explode();
            }
            this.bukkitMe.setVelocity(velocity);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::velocity, 1);
        }
    }

    private void sound() {
        if (this.me.isAlive()) {
            switch (soundTick++) {
                case 0:
                    sound(1.5f);
                case 1:
                    sound(1.5f);
                case 2:
                    sound(1.3f);
                case 3:
                    sound(1.5f);
                case 4:
                    sound(1.3f);
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::sound, 2);
        }
    }

    private void sound(float pitch) {
        this.bukkitMe.getWorld().playSound(this.bukkitMe.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.HOSTILE, 2, pitch);
    }

    private void firstSound() {
        this.bukkitMe.getWorld().playSound(this.bukkitMe.getLocation(), Sound.ENTITY_GHAST_SCREAM, SoundCategory.HOSTILE, 1, 0.5f);
    }
}
