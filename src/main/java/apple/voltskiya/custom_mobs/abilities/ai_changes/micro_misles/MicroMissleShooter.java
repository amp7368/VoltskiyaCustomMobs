package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalShootMicroMissle;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MicroMissleShooter implements MobSpawnListener.SpawnModifier {
    private static final int COOLDOWN = 20 * 10;

    @Override
    public void modifySpawn(CreatureSpawnEvent event) {
        Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        if (entity instanceof EntityInsentient) {
            EntityInsentient mob = (EntityInsentient) entity;
            mob.goalSelector.a(0, new PathfinderGoalShootMicroMissle(mob, 2 * 20, 1, MissileType.LONER));
            mob.goalSelector.a(1, new PathfinderGoalShootMicroMissle(mob, COOLDOWN, 5, MissileType.FLURRY));
        }
    }

    public enum MissileType {
        FLURRY(MicroMissileConfig.SPEED, MicroMissileConfig.MIN_TICKS_TO_LIVE, MicroMissileConfig.DAMAGE_AMOUNT),
        LONER(MicroMissileConfig.SPEED * 1.25, MicroMissileConfig.MIN_TICKS_TO_LIVE * 3, MicroMissileConfig.DAMAGE_AMOUNT * 2);
        public final double damageAmount;
        public final int minTicksToLive;
        public final double speed;

        MissileType(double speed, int minTicksToLive, double damageAmount) {
            this.minTicksToLive = minTicksToLive;
            this.speed = speed;
            this.damageAmount = damageAmount;
        }
    }
}
