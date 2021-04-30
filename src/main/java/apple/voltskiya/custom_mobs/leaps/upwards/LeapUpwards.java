package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.leaps.LeapType;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class LeapUpwards {
    public static void eatSpawnEvent(CreatureSpawnEvent event, LeapType leapType) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof EntityInsentient) {
            ((EntityInsentient) creature).goalSelector.a(1, new PathfinderGoalLeapUpwards((EntityInsentient) creature, leapType, () -> creature.hurtTimestamp >= creature.ticksLived - 10, creature::isOnGround));
        }
    }
}
