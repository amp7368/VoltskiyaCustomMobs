package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootMicroMissle;
import net.minecraft.server.v1_16_R3.EntityInsentient;

public class MicroMissleShooter implements RegisteredEntityEater {
    private static final int COOLDOWN = 20 * 10;

    public void eatEntity(EntityInsentient mob) {
        mob.goalSelector.a(0, new PathfinderGoalShootMicroMissle(mob, 2 * 20, 1, MissileType.LONER));
        mob.goalSelector.a(1, new PathfinderGoalShootMicroMissle(mob, COOLDOWN, 5, MissileType.FLURRY));
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "micro_missile_shooter";
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
