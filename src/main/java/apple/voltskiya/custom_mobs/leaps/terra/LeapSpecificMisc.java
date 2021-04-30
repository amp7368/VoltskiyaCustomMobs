package apple.voltskiya.custom_mobs.leaps.terra;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class LeapSpecificMisc {
    public static void eatSpawnEvent(CreatureSpawnEvent event,  LeapConfig config) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof EntityInsentient) {
            ((EntityInsentient) creature).goalSelector.a(1, new PathfinderGoalLeapMisc((EntityInsentient) creature, config, () -> creature.hurtTimestamp >= creature.ticksLived - 10, creature::isOnGround));
        }
    }
}
