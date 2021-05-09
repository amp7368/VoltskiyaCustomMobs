package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.SpawnEater;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootMicroMissle;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MicroMissleShooter extends SpawnEater {
    private static final int COOLDOWN = 20 * 10;

    public MicroMissleShooter() {
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

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        @Nullable Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        if (entity instanceof EntityInsentient) {
            eatEntity((EntityInsentient) entity);
        }
    }

    private void eatEntity(EntityInsentient mob) {
        mob.goalSelector.a(0, new PathfinderGoalShootMicroMissle(mob, 2 * 20, 1, MissileType.LONER));
        mob.goalSelector.a(1, new PathfinderGoalShootMicroMissle(mob, COOLDOWN, 5, MissileType.FLURRY));
        addMobs(mob.getUniqueID());
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "micro_missile_shooter";
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
        return MobTickPlugin.get();
    }

    public enum MissileType {
        FLURRY(MicroMissileConfig.SPEED, MicroMissileConfig.MIN_TICKS_TO_LIVE, MicroMissileConfig.DAMAGE_AMOUNT),
        LONER(MicroMissileConfig.SPEED * 1.25, MicroMissileConfig.MIN_TICKS_TO_LIVE * 3, MicroMissileConfig.DAMAGE_AMOUNT * 2);
        public final double damageAmount;
        public final int minTicksToLive;
        public final double speed;

        MissileType(double speed, int minTicksToLive, double damageAmount) {
            this.minTicksToLive = minTicksToLive;
            this.speed = speed;
            this.damageAmount = damageAmount;
        }
    }
}
