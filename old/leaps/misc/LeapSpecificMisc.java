package apple.voltskiya.custom_mobs.leaps.misc;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.leaps.GoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.sql.SQLException;


public class LeapSpecificMisc {

    public static void eatSpawnEvent(CreatureSpawnEvent event, String name, LeapPreConfig config) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (creature instanceof Mob) {
            eatEntity((Mob) creature, config);
            try {
                MobListSql.addMob(name, creature.getBukkitEntity().getUniqueId());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void eatEntity(Mob creature, LeapPreConfig config) {
        LeapPostConfig postConfig = new LeapPostConfig(
            (leapDo) -> DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10,
            () -> DecodeEntity.isOnGround(creature),
            LeapSpecificMisc::preLeap,
            LeapSpecificMisc::interruptedLeap,
            LeapSpecificMisc::endLeap
        );
        DecodeEntity.getGoalSelector(creature).a(0, new GoalLeap(creature, config, postConfig));
    }

    private static void preLeap(Mob entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(Mob entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    private static void interruptedLeap(Mob entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }
}
