package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs.FireFangsManager;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.world.entity.EntityInsentient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ShootBallManager extends ConfigManager implements RegisteredEntityEater {
    public Map<String, ShootersType> tagToShootType;

    public ShootBallManager() throws IOException {
        ShootersType.NORMAL.range = (double) getValueOrInit(FireFangsManager.YmlSettings.NORMAL_RANGE.getPath());
        ShootersType.NORMAL.step = (double) getValueOrInit(FireFangsManager.YmlSettings.NORMAL_STEP.getPath());
        ShootersType.NORMAL.cooldown = (int) getValueOrInit(FireFangsManager.YmlSettings.NORMAL_COOLDOWN.getPath());
        tagToShootType = new HashMap<>() {{
            put("fireball_laser", ShootersType.NORMAL);
        }};
    }

    @Override
    public void eatEntity(EntityInsentient entity) {
        for (String tag : entity.getScoreboardTags()) {
            ShootersType type = tagToShootType.get(tag);
            if (type != null) {
                DecodeEntity.getGoalSelector(entity).a(0, new PathfinderGoalShootSpell<>(new ShootBallCaster(entity), type));
            }
        }
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "shoot_ball";
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
        NORMAL_RANGE("normal.range", 40d),
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

    public enum ShootersType implements PathfinderGoalShootSpell.SpellType<ShootBallCaster> {
        NORMAL(0, 0, 0, ShootBallSpell::new),
        ;
        private final BiFunction<ShootBallCaster, ShootersType, ShootBallSpell> runnableConstructor;
        private double range;
        private double step;
        private int cooldown;

        ShootersType(double range, double step, int cooldown, BiFunction<ShootBallCaster, ShootersType, ShootBallSpell> runnableConstructor) {
            this.range = range;
            this.step = step;
            this.cooldown = cooldown;
            this.runnableConstructor = runnableConstructor;
        }

        public double getStep() {
            return step;
        }

        public int getCooldown() {
            return cooldown;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= range;
        }

        public ShootBallSpell construct(ShootBallCaster me) {
            return runnableConstructor.apply(me, this);
        }

        public double getRange() {
            return this.range;
        }

        public int getChargeUpTicks() {
            return 80;
        }

        public double getShotSpeed() {
            return 3.4;
        }

        public int getShotsToTake() {
            return 15;
        }

        public int getTimeToShoot() {
            return 20;
        }
    }
}
