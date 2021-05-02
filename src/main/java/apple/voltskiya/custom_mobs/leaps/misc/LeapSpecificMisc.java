package apple.voltskiya.custom_mobs.leaps.misc;

import apple.voltskiya.custom_mobs.leaps.PathfinderGoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;


public class LeapSpecificMisc {
    public static void eatSpawnEvent(CreatureSpawnEvent event, LeapPreConfig config) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof EntityInsentient) {
            LeapPostConfig postConfig = new LeapPostConfig(
                    (leapDo) -> creature.hurtTimestamp >= creature.ticksLived - 10,
                    creature::isOnGround,
                    LeapSpecificMisc::preLeap,
                    LeapSpecificMisc::interruptedLeap,
                    LeapSpecificMisc::endLeap
            );
            ((EntityInsentient) creature).goalSelector.a(0, new PathfinderGoalLeap("misc",(EntityInsentient) creature, config, postConfig));
        }
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(EntityInsentient entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    private static void interruptedLeap(EntityInsentient entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }
}
