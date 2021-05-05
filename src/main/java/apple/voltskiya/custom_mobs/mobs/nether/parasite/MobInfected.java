package apple.voltskiya.custom_mobs.mobs.nether.parasite;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class MobInfected {
    public static final String PARASITE_INFECTED_TAG = "PARASITE_INFECTED";
    private static final long EXPLODE_TO_PARASITES_DELAY = 20 * 30;
    private EntityCreature entity;

    public MobInfected(EntityCreature entity) {
        this.entity = entity;
        this.entity.addScoreboardTag(PARASITE_INFECTED_TAG);
        this.rabidAI();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> this.explodeToParasites(3, false), EXPLODE_TO_PARASITES_DELAY);
        this.sound(this.entity.getBukkitEntity());
        this.particles();
    }

    private void particles() {
        if (this.entity == null) return;
        if (!this.entity.isAlive()) {
            explodeToParasites(1, true);
            return;
        }
        final CraftEntity bukkitEntity = this.entity.getBukkitEntity();
        double x = bukkitEntity.getLocation().getX();
        double y = bukkitEntity.getLocation().getY();
        double z = bukkitEntity.getLocation().getZ();
        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            double xi = (random.nextDouble() - .5) * 1;
            double yi = (random.nextDouble() - .5) * 1;
            double zi = (random.nextDouble() - .5) * 1;
            bukkitEntity.getLocation().getWorld().spawnParticle(Particle.CRIMSON_SPORE, x + xi, y + yi, z + zi, 2, 0, 0, 0, 0.04);
        }
        if (random.nextInt(80) == 0) sound(bukkitEntity);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::particles, 3);
    }

    private void sound(CraftEntity bukkitEntity) {
        bukkitEntity.getWorld().playSound(bukkitEntity.getLocation(), Sound.ENTITY_ENDERMITE_AMBIENT, SoundCategory.HOSTILE, 1, 1.4f);
    }

    private void explodeToParasites(int count, boolean force) {
        if (entity == null) return;
        if (!force && !this.entity.isAlive()) return;
        final CraftEntity bukkitEntity = this.entity.getBukkitEntity();
        double x = bukkitEntity.getLocation().getX();
        double y = bukkitEntity.getLocation().getY();
        double z = bukkitEntity.getLocation().getZ();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            double xi = (random.nextDouble() - .5) * 2.25;
            double yi = (random.nextDouble() - .5) * 2.25;
            double zi = (random.nextDouble() - .5) * 2.25;
            bukkitEntity.getLocation().getWorld().spawnParticle(Particle.CRIMSON_SPORE, x + xi, y + yi, z + zi, 2, 0, 0, 0, 0.04);
            Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(random.nextBoolean() ? 99 : 172, 0, 0), 1.5f);
            bukkitEntity.getLocation().getWorld().spawnParticle(Particle.REDSTONE, x + xi, y + yi, z + zi, 2, 0, 0, 0, 0.04, dust);
        }
        this.entity.die();
        bukkitEntity.getWorld().playSound(bukkitEntity.getLocation(), Sound.ENTITY_ENDERMITE_DEATH, SoundCategory.HOSTILE, 1, 0.1f);
        bukkitEntity.getWorld().playSound(bukkitEntity.getLocation(), Sound.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.HOSTILE, 0.5f, 1f);
        for (int i = 0; i < count; i++) {
            MobParasite.spawn(bukkitEntity.getLocation(), null, randomVelocity());
        }
        this.entity = null;
    }

    private Vec3D randomVelocity() {
        Random random = new Random();
        return new Vec3D(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5);
    }

    private void rabidAI() {
        this.entity.goalSelector = new PathfinderGoalSelector(entity.world.getMethodProfilerSupplier());
        this.entity.targetSelector = new PathfinderGoalSelector(entity.world.getMethodProfilerSupplier());
        this.entity.goalSelector.a(5, new PathfinderGoalRandomStrollLand(entity, 1.5D));
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        new MobInfected(((CraftCreature)event.getEntity()).getHandle());
    }
}
