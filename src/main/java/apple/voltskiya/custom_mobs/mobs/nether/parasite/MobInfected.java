package apple.voltskiya.custom_mobs.mobs.nether.parasite;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;

import java.util.Random;

public class MobInfected {
    public static final String PARASITE_INFECTED_TAG = "PARASITE_INFECTED";
    private static final long EXPLODE_TO_PARASITES_DELAY = 20 * 60 * 2;
    private final EntityCreature entity;

    public MobInfected(EntityCreature entity) {
        this.entity = entity;
        this.entity.addScoreboardTag(PARASITE_INFECTED_TAG);
        this.rabidAI();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::explodeToParasites, EXPLODE_TO_PARASITES_DELAY);
    }

    private void explodeToParasites() {
        if (!this.entity.isAlive()) return;
        this.entity.die(DamageSource.STARVE);
        final CraftEntity bukkitEntity = this.entity.getBukkitEntity();
        double x = bukkitEntity.getLocation().getX();
        double y = bukkitEntity.getLocation().getY();
        double z = bukkitEntity.getLocation().getZ();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            double xi = (random.nextDouble() - .5) * 2;
            double yi = (random.nextDouble() - .5) * 2;
            double zi = (random.nextDouble() - .5) * 2;
            bukkitEntity.getLocation().getWorld().spawnParticle(Particle.CRIMSON_SPORE, x + xi, y + yi, z + zi, 1);
        }
        MobParasite.spawn(bukkitEntity.getLocation(), null);
    }

    private void rabidAI() {
        this.entity.goalSelector = new PathfinderGoalSelector(entity.world.getMethodProfilerSupplier());
        this.entity.targetSelector = new PathfinderGoalSelector(entity.world.getMethodProfilerSupplier());
        this.entity.goalSelector.a(5, new PathfinderGoalRandomStrollLand(entity, 1.5D));
    }
}
