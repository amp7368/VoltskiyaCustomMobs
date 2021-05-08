package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.SpawnEater;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootFireFangs;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireFangs extends SpawnEater {
    public Map<String, FangsType> tagToFangType;

    public FireFangs() throws IOException {
        FangsType.NORMAL.range = (double) getValueOrInit(YmlSettings.NORMAL_RANGE.getPath());
        FangsType.NORMAL.step = (double) getValueOrInit(YmlSettings.NORMAL_STEP.getPath());
        FangsType.NORMAL.cooldown = (int) getValueOrInit(YmlSettings.NORMAL_COOLDOWN.getPath());
        FangsType.TRIPLE.range = (double) getValueOrInit(YmlSettings.TRIPLE_RANGE.getPath());
        FangsType.TRIPLE.step = (double) getValueOrInit(YmlSettings.TRIPLE_STEP.getPath());
        FangsType.TRIPLE.cooldown = (int) getValueOrInit(YmlSettings.TRIPLE_COOLDOWN.getPath());
        FangsType.BLUE_NORMAL.range = (double) getValueOrInit(YmlSettings.BLUE_NORMAL_RANGE.getPath());
        FangsType.BLUE_NORMAL.step = (double) getValueOrInit(YmlSettings.BLUE_NORMAL_STEP.getPath());
        FangsType.BLUE_NORMAL.cooldown = (int) getValueOrInit(YmlSettings.BLUE_NORMAL_COOLDOWN.getPath());
        FangsType.BLUE_TRIPLE.range = (double) getValueOrInit(YmlSettings.BLUE_TRIPLE_RANGE.getPath());
        FangsType.BLUE_TRIPLE.step = (double) getValueOrInit(YmlSettings.BLUE_TRIPLE_STEP.getPath());
        FangsType.BLUE_TRIPLE.cooldown = (int) getValueOrInit(YmlSettings.BLUE_TRIPLE_COOLDOWN.getPath());
        tagToFangType = new HashMap<>() {{
            put("fire_fangs", FangsType.NORMAL);
            put("fire_fangs_triple", FangsType.TRIPLE);
            put("fire_fangs_blue", FangsType.BLUE_NORMAL);
            put("fire_fangs_triple_blue", FangsType.BLUE_TRIPLE);
        }};
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
        for (String tag : entity.getScoreboardTags()) {
            FangsType type = tagToFangType.get(tag);
            if (type != null) {
                entity.goalSelector.a(0, new PathfinderGoalShootFireFangs(entity, type));
            }
        }
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
        NORMAL_RANGE("normal.range", 15d),
        NORMAL_STEP("normal.step", 1d),
        NORMAL_COOLDOWN("normal.cooldown", 300),
        TRIPLE_RANGE("triple.range", 15d),
        TRIPLE_STEP("triple.step", 1d),
        TRIPLE_COOLDOWN("triple.cooldown", 300),
        BLUE_NORMAL_RANGE("blue_normal.range", 15d),
        BLUE_NORMAL_STEP("blue_normal.step", 1d),
        BLUE_NORMAL_COOLDOWN("blue_normal.cooldown", 300),
        BLUE_TRIPLE_RANGE("blue_triple.range", 15d),
        BLUE_TRIPLE_STEP("blue_triple.step", 1d),
        BLUE_TRIPLE_COOLDOWN("blue_triple.cooldown", 300);

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
        NORMAL(0, 0, 0,false),
        TRIPLE(0, 0, 0,false),
        BLUE_NORMAL(0, 0, 0,true),
        BLUE_TRIPLE(0, 0, 0,true); //default to 0 until someone sets it outside of us

        private final boolean isBlue;
        private double range;
        private double step;
        private int cooldown;

        FangsType(double range, double step, int cooldown, boolean isBlue) {
            this.range = range;
            this.step = step;
            this.cooldown = cooldown;
            this.isBlue = isBlue;
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
        public boolean isBlue(){
            return isBlue;
        }
    }
}
