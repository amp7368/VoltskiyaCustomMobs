package apple.voltskiya.custom_mobs.leaps.misc;

import apple.voltskiya.custom_mobs.leaps.PathfinderGoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.sql.SQLException;


public class LeapSpecificMisc {
    public static void eatSpawnEvent(CreatureSpawnEvent event, String name, LeapPreConfig config) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof EntityInsentient) {
            eatEntity((EntityInsentient) creature, config);
            try {
                MobListSql.addMob(name, creature.getUniqueID());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void eatEntity(EntityInsentient creature, LeapPreConfig config) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> creature.hurtTimestamp >= creature.ticksLived - 10,
                creature::isOnGround,
                LeapSpecificMisc::preLeap,
                LeapSpecificMisc::interruptedLeap,
                LeapSpecificMisc::endLeap
        );
        creature.goalSelector.a(0, new PathfinderGoalLeap(creature, config, postConfig));
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
