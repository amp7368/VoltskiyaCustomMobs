package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

import apple.nms.decoding.entity.DecodeEntity;
import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.function.BiFunction;
import net.minecraft.world.entity.Mob;

public class FlameThrowerManager implements SpawnHandlerListener {

    @Override
    public String getTag() {
        return "flamethower";
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        Mob entity = (Mob) mmSpawned.getNmsEntity();
        DecodeEntity.getGoalSelector(entity).addGoal(0,
            new PathfinderGoalShootSpell<>(new FlameThrowerCaster(entity),
                FlamethrowerType.NORMAL));
    }


    public enum FlamethrowerType implements PathfinderGoalShootSpell.SpellType<FlameThrowerCaster> {
        NORMAL(FlameThrowerConfig.get(), FlameThrowerSpell::new);
        private final FlameThrowerConfig config;
        private final BiFunction<FlameThrowerCaster, FlamethrowerType, FlameThrowerSpell> runnableConstructor;

        FlamethrowerType(FlameThrowerConfig config,
            BiFunction<FlameThrowerCaster, FlamethrowerType, FlameThrowerSpell> runnableConstructor) {
            this.config = config;
            this.runnableConstructor = runnableConstructor;
        }

        @Override
        public int getCooldown() {
            return config.cooldown;
        }

        @Override
        public boolean inRange(double distance) {
            return NumberUtils.betweenDouble(config.minRange, distance, config.range);
        }

        @Override
        public FlameThrowerSpell construct(FlameThrowerCaster me) {
            return runnableConstructor.apply(me, this);
        }
    }
}
