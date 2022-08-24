package apple.voltskiya.custom_mobs.leaps.hellish_catalyst;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class LeapHellishCatalyst implements LeapEater {
    private static void endLeap(Mob entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        DecodeEntity.setGoalTarget(entity, lastTarget);
    }

    private static boolean shouldStopLeap(EntityLiving creature) {
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void preLeap(Mob entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void interruptedLeap(Mob entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        DecodeEntity.setGoalTarget(entity, lastTarget);
    }

    public void eatEntity(Mob creature) {
        @Nullable EntityLiving lastTarget = DecodeEntity.getLastTarget(creature);
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                () -> DecodeEntity.isOnGround(creature),
                LeapHellishCatalyst::preLeap,
                (entity) -> LeapHellishCatalyst.interruptedLeap(entity, lastTarget),
                (entity) -> LeapHellishCatalyst.endLeap(entity, lastTarget)
        );
        DecodeEntity.getGoalSelector(creature).a(0, new GoalLeapCatalyst(creature, getConfig(), postConfig));
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "hellish_catalyst_leap";
    }
}
