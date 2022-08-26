package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs.FireFangsConfig.FireFangsTypeConfig;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.world.entity.Mob;

public class FireFangsManager implements SpawnHandlerListener {

    public Map<String, FangsType> tagToFangType;

    public FireFangsManager() {
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
    public String getTag() {
        return "fire_fangs";
    }


    @Override
    public void handle(MMSpawned mmSpawned) {
        Mob entity = (Mob) mmSpawned.getNmsEntity();
        for (String tag : mmSpawned.getEntity().getScoreboardTags()) {
            FangsType type = tagToFangType.get(tag);
            if (type == null) {
                continue;
            }
            DecodeEntity.getGoalSelector(entity)
                .addGoal(0, new PathfinderGoalShootSpell<>(new FireFangsCaster(entity), type));
        }
    }

    public enum FangsType implements PathfinderGoalShootSpell.SpellType<FireFangsCaster> {
        NORMAL(FireFangsConfig.get().normal, false, FireFangsSpell::new),
        TRIPLE(FireFangsConfig.get().triple, false, FireFangsSpell::new),
        TRIPLE_STRAIGHT(FireFangsConfig.get().tripleStraight, false, FireFangsSpellStraight::new),
        BLUE_NORMAL(FireFangsConfig.get().blueNormal, true, FireFangsSpell::new),
        BLUE_TRIPLE(FireFangsConfig.get().blueTriple, true, FireFangsSpell::new),
        BLUE_TRIPLE_STRAIGHT(FireFangsConfig.get().blueTripleStraight, true,
            FireFangsSpellStraight::new);

        private final boolean isBlue;
        private final BiFunction<FireFangsCaster, FangsType, FireFangsSpell> runnableConstructor;
        private final FireFangsTypeConfig config;

        FangsType(FireFangsTypeConfig config, boolean isBlue,
            BiFunction<FireFangsCaster, FangsType, FireFangsSpell> runnableConstructor) {
            this.config = config;
            this.isBlue = isBlue;
            this.runnableConstructor = runnableConstructor;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= getRange();
        }

        public double getStep() {
            return config.step;
        }

        public int getCooldown() {
            return config.cooldown;
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
            return config.range;
        }
    }
}
