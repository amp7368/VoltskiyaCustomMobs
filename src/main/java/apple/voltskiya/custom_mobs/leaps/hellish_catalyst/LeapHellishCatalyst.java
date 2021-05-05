package apple.voltskiya.custom_mobs.leaps.hellish_catalyst;

import apple.voltskiya.custom_mobs.ConfigManager;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.UUID;

public class LeapHellishCatalyst extends ConfigManager {
    private static LeapHellishCatalyst instance;

    public LeapHellishCatalyst() {
        instance = this;
        for (UUID mob : getMobs()) {
            final CraftEntity entityBukkit = (CraftEntity) Bukkit.getEntity(mob);
            if (entityBukkit != null) {
                @org.jetbrains.annotations.Nullable Entity entity = entityBukkit.getHandle();
                if (entity instanceof EntityInsentient) {
                    eatEntity((EntityInsentient) entity);
                    continue;
                }
            }
            MobListSql.removeMob(mob);
        }
    }

    public static void eatSpawnEvent(CreatureSpawnEvent event) {
        EntityLiving creature = ((CraftLivingEntity) event.getEntity()).getHandle();
        eatEntity(creature);
    }

    private static void eatEntity(EntityLiving creature) {
        if (creature instanceof EntityInsentient) {
            @Nullable EntityLiving lastTarget = ((EntityInsentient) creature).getGoalTarget();

            LeapPostConfig postConfig = new LeapPostConfig(
                    (leapDo) -> shouldStopLeap(creature),
                    creature::isOnGround,
                    LeapHellishCatalyst::preLeap,
                    (entity) -> LeapHellishCatalyst.interruptedLeap(entity, lastTarget),
                    (entity) -> LeapHellishCatalyst.endLeap(entity, lastTarget)
            );
            ((EntityInsentient) creature).goalSelector.a(0, new PathfinderGoalLeapCatalyst((EntityInsentient) creature, LeapType.HELLISH_CATALYST_LEAP.getLeapConfig(), postConfig));
            instance.addMobs(creature.getUniqueID());
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

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }
}
