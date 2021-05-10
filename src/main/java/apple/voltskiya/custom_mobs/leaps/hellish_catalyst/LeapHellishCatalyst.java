package apple.voltskiya.custom_mobs.leaps.hellish_catalyst;

import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class LeapHellishCatalyst implements LeapEater {
    public void eatEntity(EntityInsentient creature) {
        @Nullable EntityLiving lastTarget = creature.getGoalTarget();

        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                creature::isOnGround,
                LeapHellishCatalyst::preLeap,
                (entity) -> LeapHellishCatalyst.interruptedLeap(entity, lastTarget),
                (entity) -> LeapHellishCatalyst.endLeap(entity, lastTarget)
        );
        creature.goalSelector.a(0, new PathfinderGoalLeapCatalyst(creature, getConfig(), postConfig));
    }

    private static boolean shouldStopLeap(EntityLiving creature) {
        return creature.hurtTimestamp >= creature.ticksLived - 10;
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        entity.setGoalTarget(lastTarget);
    }

    private static void interruptedLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        entity.setGoalTarget(lastTarget);
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "hellish_catalyst_leap";
    }
}
