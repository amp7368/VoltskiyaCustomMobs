package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.sounds.LeapSounds;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;


public class LeapUpwards {
    public static void eatSpawnEvent(CreatureSpawnEvent event, LeapType leapType) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> creature.hurtTimestamp >= creature.ticksLived - 10,
                creature::isOnGround,
                LeapUpwards::preLeap,
                LeapUpwards::interruptedLeap,
                LeapUpwards::endLeap
        );
        if (creature instanceof EntityInsentient) {
            ((EntityInsentient) creature).goalSelector.a(5, new PathfinderGoalLeapUpwards((EntityInsentient) creature, leapType, postConfig));
        }
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        Location location = entity.getBukkitEntity().getLocation();
        LeapSounds.CHARGE_UP_GROWL.accept(location);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), leapDo::leap, 10);
    }

    private static void interruptedLeap(EntityInsentient entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    private static void endLeap(EntityInsentient entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

}
