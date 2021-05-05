package apple.voltskiya.custom_mobs.leaps.revenant;

import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public class LeapRevenant {
    public static void eatSpawnEvent(CreatureSpawnEvent event, LeapType leapType) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof EntityInsentient) {

            LeapPostConfig postConfig = new LeapPostConfig(
                    (leapDo) -> shouldStopLeap(creature),
                    creature::isOnGround,
                    LeapRevenant::preLeap,
                    LeapRevenant::interruptedLeap,
                    (entity) -> LeapRevenant.endLeap(entity, entity.getGoalTarget())
            );
            ((EntityInsentient) creature).goalSelector.a(0, new PathfinderGoalLeapRevenant((EntityInsentient) creature, leapType.getLeapConfig(), postConfig));
        }
    }

    private static boolean shouldStopLeap(EntityLiving creature) {
        return creature.hurtTimestamp >= creature.ticksLived - 10;
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        if (lastTarget != null)
            entity.setGoalTarget(lastTarget);
    }

    private static void interruptedLeap(EntityInsentient entity) {
    }
}
