package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Mob;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ShootBallManager extends ConfigManager implements RegisteredEntityEater {
    public Map<String, ShootersType> tagToShootType;

    public ShootBallManager() throws IOException {
        ShootersType.NORMAL.range = (double) getValueOrInit(YmlSettings.NORMAL_RANGE.getPath());
        ShootersType.NORMAL.step = (double) getValueOrInit(YmlSettings.NORMAL_STEP.getPath());
        ShootersType.NORMAL.cooldown = (int) getValueOrInit(YmlSettings.NORMAL_COOLDOWN.getPath());
        ShootersType.OVERSEER.range = (double) getValueOrInit(YmlSettings.OVERSEER_RANGE.getPath());
        ShootersType.OVERSEER.step = (double) getValueOrInit(YmlSettings.OVERSEER_STEP.getPath());
        ShootersType.OVERSEER.cooldown = (int) getValueOrInit(YmlSettings.OVERSEER_COOLDOWN.getPath());
        tagToShootType = new HashMap<>() {{
            put("fireball_laser", ShootersType.NORMAL);
        }};
    }

    @Override
    public void eatEntity(EntityInsentient entity) {
        for (String tag : entity.getBukkitEntity().getScoreboardTags()) {
            ShootersType type = tagToShootType.get(tag);
            if (type != null) {
                if (entity.getBukkitEntity().getScoreboardTags().contains("overseer_boss"))
                    type = ShootersType.OVERSEER;
                final CraftEntity bukkitEntity = entity.getBukkitEntity();
                if (bukkitEntity instanceof Mob mob) {
                    final AttributeInstance followRange = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                    if (followRange != null) {
                        followRange.setBaseValue(Math.max(followRange.getBaseValue(), type.getRange()));
                    }
                }
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
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
    }

    public enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings {
        NORMAL_RANGE("normal.range", 50d),
        NORMAL_STEP("normal.step", 1d),
        NORMAL_COOLDOWN("normal.cooldown", 300),
        OVERSEER_RANGE("overseer.range", 50d),
        OVERSEER_STEP("overseer.step", 1d),
        OVERSEER_COOLDOWN("overseer.cooldown", 600);
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
        OVERSEER(0, 0, 0, ShootBallSpell::new);
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
            return 0.9;
        }

        public int getShotsToTake() {
            return 15;
        }

        public int getTimeToShoot() {
            return 20;
        }
    }
}
