package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsConfig.FireFangsTypeConfig;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import java.util.function.BiFunction;

public enum FireFangsType implements PathfinderGoalShootSpell.SpellType<FireFangsCaster> {
    NORMAL(FireFangsConfig.get().normal, false, FireFangsSpell::new),
    TRIPLE(FireFangsConfig.get().triple, false, FireFangsSpell::new),
    TRIPLE_STRAIGHT(FireFangsConfig.get().tripleStraight, false, FireFangsSpellStraight::new),
    BLUE_NORMAL(FireFangsConfig.get().blueNormal, true, FireFangsSpell::new),
    BLUE_TRIPLE(FireFangsConfig.get().blueTriple, true, FireFangsSpell::new),
    BLUE_TRIPLE_STRAIGHT(FireFangsConfig.get().blueTripleStraight, true,
        FireFangsSpellStraight::new);

    private final boolean isBlue;
    private final BiFunction<FireFangsCaster, FireFangsType, FireFangsSpell> runnableConstructor;
    private final FireFangsTypeConfig config;

    FireFangsType(FireFangsTypeConfig config, boolean isBlue,
        BiFunction<FireFangsCaster, FireFangsType, FireFangsSpell> runnableConstructor) {
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
