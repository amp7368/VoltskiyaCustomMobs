package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.SpawnEater;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalShootFireFangs;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class FireFangs extends SpawnEater {
    private static int NORMAL_COOLDOWN;
    private static double NORMAL_STEP;
    private static double NORMAL_RANGE;

    public FireFangs() throws IOException {
        NORMAL_RANGE = (double) getValueOrInit(YmlSettings.NORMAL_RANGE.getPath());
        NORMAL_STEP = (double) getValueOrInit(YmlSettings.NORMAL_STEP.getPath());
        NORMAL_COOLDOWN = (int) getValueOrInit(YmlSettings.NORMAL_COOLDOWN.getPath());

        for (UUID mob : this.getMobs()) {
            final CraftEntity entityBukkit = (CraftEntity) Bukkit.getEntity(mob);
            if (entityBukkit != null) {
                @Nullable Entity entity = entityBukkit.getHandle();
                if (entity instanceof EntityInsentient) {
                    this.eatEntity((EntityInsentient) entity);
                    continue;
                }
            }
            MobListSql.removeMob(mob);
        }
    }

    private void eatEntity(EntityInsentient entity) {
        entity.goalSelector.a(0, new PathfinderGoalShootFireFangs(entity,  FangsType.NORMAL));
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        @Nullable Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        if (entity instanceof EntityInsentient) {
            this.eatEntity((EntityInsentient) entity);
        }
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "fire_fangs";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }

    public enum YmlSettings implements apple.voltskiya.custom_mobs.YmlSettings {
        NORMAL_RANGE("normal.range", 3d),
        NORMAL_STEP("normal.step", 1d),
        NORMAL_COOLDOWN("normal.cooldown", 300);

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }

    public enum FangsType {
        NORMAL(FireFangs.NORMAL_RANGE, FireFangs.NORMAL_STEP, FireFangs.NORMAL_COOLDOWN);

        private final double range;
        private final double step;
        private final int cooldown;

        FangsType(double range, double step, int cooldown) {
            this.range = range;
            this.step = step;
            this.cooldown = cooldown;
        }

        public double getRange() {
            return range;
        }

        public double getStep() {
            return step;
        }

        public int getCooldown() {
            return cooldown;
        }

        public int getFireLength() {
            return 250;
        }
    }
}
