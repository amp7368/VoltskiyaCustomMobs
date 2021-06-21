package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.world.entity.EntityInsentient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FireFangsManager extends ConfigManager implements RegisteredEntityEater {
    public Map<String, FangsType> tagToFangType;

    public FireFangsManager() throws IOException {
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
        FangsType.BLUE_TRIPLE_STRAIGHT.range = (double) getValueOrInit(YmlSettings.BLUE_TRIPLE_STRAIGHT_RANGE.getPath());
        FangsType.BLUE_TRIPLE_STRAIGHT.step = (double) getValueOrInit(YmlSettings.BLUE_TRIPLE_STRAIGHT_STEP.getPath());
        FangsType.BLUE_TRIPLE_STRAIGHT.cooldown = (int) getValueOrInit(YmlSettings.BLUE_TRIPLE_STRAIGHT_COOLDOWN.getPath());
        FangsType.TRIPLE_STRAIGHT.range = (double) getValueOrInit(YmlSettings.TRIPLE_STRAIGHT_RANGE.getPath());
        FangsType.TRIPLE_STRAIGHT.step = (double) getValueOrInit(YmlSettings.TRIPLE_STRAIGHT_STEP.getPath());
        FangsType.TRIPLE_STRAIGHT.cooldown = (int) getValueOrInit(YmlSettings.TRIPLE_STRAIGHT_COOLDOWN.getPath());
        tagToFangType = new HashMap<>() {{
            put("fire_fangs_basic", FangsType.NORMAL);
            put("fire_fangs_triple", FangsType.TRIPLE);
            put("fire_fangs_triple_straight", FangsType.TRIPLE_STRAIGHT);
            put("fire_fangs_basic_blue", FangsType.BLUE_NORMAL);
            put("fire_fangs_triple_blue", FangsType.BLUE_TRIPLE);
            put("fire_fangs_triple_blue_straight", FangsType.BLUE_TRIPLE_STRAIGHT);
        }};

    }

    @Override
    public void eatEntity(EntityInsentient entity) {
        for (String tag : entity.getScoreboardTags()) {
            FangsType type = tagToFangType.get(tag);
            if (type != null) {
                DecodeEntity.getGoalSelector(entity).a(0, new PathfinderGoalShootSpell<>(new FireFangsCaster(entity), type));
            }
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

    public enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
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
        BLUE_TRIPLE_COOLDOWN("blue_triple.cooldown", 300),
        BLUE_TRIPLE_STRAIGHT_RANGE("blue_triple_straight.range", 15d),
        BLUE_TRIPLE_STRAIGHT_STEP("blue_triple_straight.step", 1d),
        BLUE_TRIPLE_STRAIGHT_COOLDOWN("blue_triple_straight.cooldown", 300),
        TRIPLE_STRAIGHT_RANGE("triple_straight.range", 15d),
        TRIPLE_STRAIGHT_STEP("triple_straight.step", 1d),
        TRIPLE_STRAIGHT_COOLDOWN("triple_straight.cooldown", 300);

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

    public enum FangsType implements PathfinderGoalShootSpell.SpellType<FireFangsCaster> {
        NORMAL(0, 0, 0, false, FireFangsSpell::new),
        TRIPLE(0, 0, 0, false, FireFangsSpell::new),
        TRIPLE_STRAIGHT(0, 0, 0, false, FireFangsSpellStraight::new),
        BLUE_NORMAL(0, 0, 0, true, FireFangsSpell::new),
        BLUE_TRIPLE(0, 0, 0, true, FireFangsSpell::new),
        BLUE_TRIPLE_STRAIGHT(0, 0, 0, true, FireFangsSpellStraight::new); //default to 0 until someone sets it outside of us

        private final boolean isBlue;
        private final BiFunction<FireFangsCaster, FangsType, FireFangsSpell> runnableConstructor;
        private double range;
        private double step;
        private int cooldown;

        FangsType(double range, double step, int cooldown, boolean isBlue, BiFunction<FireFangsCaster, FangsType, FireFangsSpell> runnableConstructor) {
            this.range = range;
            this.step = step;
            this.cooldown = cooldown;
            this.isBlue = isBlue;
            this.runnableConstructor = runnableConstructor;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= range;
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

        public boolean isBlue() {
            return isBlue;
        }

        public FireFangsSpell construct(FireFangsCaster me) {
            return runnableConstructor.apply(me, this);
        }

        public double getRange() {
            return this.range;
        }
    }
}
