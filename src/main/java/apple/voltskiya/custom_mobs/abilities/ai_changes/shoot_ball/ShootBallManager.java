package apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball.ShootBallConfig.ShootBallTypeConfig;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Mob;

public class ShootBallManager implements SpawnHandlerListener {

    public Map<String, ShootersType> tagToShootType;

    public ShootBallManager() throws IOException {
        tagToShootType = new HashMap<>() {{
            put("fireball_laser", ShootersType.NORMAL);
        }};
    }

    @Override
    public String getTag() {
        return "shoot_ball";
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        net.minecraft.world.entity.Mob entity = mmSpawned.getNmsMob();
        for (String tag : entity.getBukkitEntity().getScoreboardTags()) {
            ShootersType type = tagToShootType.get(tag);
            if (type != null) {
                if (entity.getBukkitEntity().getScoreboardTags().contains("overseer_boss"))
                    type = ShootersType.OVERSEER;
                final CraftEntity bukkitEntity = entity.getBukkitEntity();
                if (bukkitEntity instanceof Mob mob) {
                    final AttributeInstance followRange = mob.getAttribute(
                        Attribute.GENERIC_FOLLOW_RANGE);
                    if (followRange != null) {
                        followRange.setBaseValue(
                            Math.max(followRange.getBaseValue(), type.getRange()));
                    }
                }
                DecodeEntity.getGoalSelector(entity)
                    .addGoal(0, new PathfinderGoalShootSpell<>(new ShootBallCaster(entity), type));
            }
        }
    }

    public enum ShootersType implements PathfinderGoalShootSpell.SpellType<ShootBallCaster> {
        NORMAL(ShootBallConfig.get().normal, ShootBallSpell::new),
        OVERSEER(ShootBallConfig.get().overseer, ShootBallSpell::new);
        private final BiFunction<ShootBallCaster, ShootersType, ShootBallSpell> runnableConstructor;
        private ShootBallTypeConfig config;

        ShootersType(ShootBallTypeConfig config,
            BiFunction<ShootBallCaster, ShootersType, ShootBallSpell> runnableConstructor) {
            this.config = config;
            this.runnableConstructor = runnableConstructor;
        }

        public double getStep() {
            return config.step;
        }

        public int getCooldown() {
            return config.cooldown;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= getRange();
        }

        public ShootBallSpell construct(ShootBallCaster me) {
            return runnableConstructor.apply(me, this);
        }

        public double getRange() {
            return this.config.range;
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
