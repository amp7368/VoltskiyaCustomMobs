package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.ConfigManager;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.sounds.LeapSounds;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class LeapUpwards extends ConfigManager {
    private static LeapUpwards instance;

    public LeapUpwards() {
        instance = this;
        for (UUID mob : getMobs()) {
            final CraftEntity entityBukkit = (CraftEntity) Bukkit.getEntity(mob);
            if (entityBukkit != null) {
                @Nullable Entity entity = entityBukkit.getHandle();
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
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> creature.hurtTimestamp >= creature.ticksLived - 10,
                creature::isOnGround,
                LeapUpwards::preLeap,
                LeapUpwards::interruptedLeap,
                LeapUpwards::endLeap
        );
        if (creature instanceof EntityInsentient) {
            ((EntityInsentient) creature).goalSelector.a(0, new PathfinderGoalLeapUpwards((EntityInsentient) creature, LeapType.UPWARDS_LEAP, postConfig));
        }
        instance.addMobs(creature.getUniqueID());
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

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "leap_upwards";
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
