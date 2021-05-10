package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.server.v1_16_R3.EntityInsentient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FlameThrowerManager extends ConfigManager implements RegisteredEntityEater {
    public Map<String, FlamethrowerType> tagToFlamethrowerType;

    public FlameThrowerManager() throws IOException {
        FlamethrowerType.NORMAL.minRange = (double) getValueOrInit(YmlSettings.NORMAL_MIN_RANGE.getPath());
        FlamethrowerType.NORMAL.range = (double) getValueOrInit(YmlSettings.NORMAL_RANGE.getPath());
        FlamethrowerType.NORMAL.cooldown = (int) getValueOrInit(YmlSettings.NORMAL_COOLDOWN.getPath());
        tagToFlamethrowerType = new HashMap<>() {
            {
                put("flamethrower_basic", FlamethrowerType.NORMAL);
            }
        };
    }

    public void eatEntity(EntityInsentient entity) {
        for (String tag : entity.getScoreboardTags()) {
            FlamethrowerType type = tagToFlamethrowerType.get(tag);
            if (type != null) {
                entity.goalSelector.a(0, new PathfinderGoalShootSpell<>(new FlameThrowerCaster(entity), type));
            }
        }
    }


    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "flamethower";
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


    public enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
        NORMAL_RANGE("normal.range", 13d),
        NORMAL_COOLDOWN("normal.cooldown", 300),
        NORMAL_MIN_RANGE("normal.min_range", 4d);
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

    public enum FlamethrowerType implements PathfinderGoalShootSpell.SpellType<FlameThrowerCaster> {
        NORMAL(0, 0, FlameThrowerSpell::new);

        public double minRange;
        private double range;
        private int cooldown;
        private final BiFunction<FlameThrowerCaster, FlamethrowerType, FlameThrowerSpell> runnableConstructor;

        FlamethrowerType(double range, int cooldown, BiFunction<FlameThrowerCaster, FlamethrowerType, FlameThrowerSpell> runnableConstructor) {
            this.range = range;
            this.cooldown = cooldown;
            this.runnableConstructor = runnableConstructor;
        }

        @Override
        public int getCooldown() {
            return cooldown;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= range;
        }

        @Override
        public FlameThrowerSpell construct(FlameThrowerCaster me) {
            return runnableConstructor.apply(me, this);
        }
    }
}
