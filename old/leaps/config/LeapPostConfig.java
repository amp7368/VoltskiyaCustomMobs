package apple.voltskiya.custom_mobs.leaps.config;

import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class LeapPostConfig {
    private final Function<LeapDo, Boolean> shouldStopCurrentLeap;
    private final BooleanSupplier isOnGround;
    private final BiConsumer<Mob, LeapDo> preLeap;
    private final Consumer<Mob> interruptedLeap;
    private final Consumer<Mob> endLeap;

    public LeapPostConfig(
            Function<LeapDo, Boolean> shouldStopCurrentLeap,
            BooleanSupplier isOnGround,
            BiConsumer<Mob, LeapDo> preLeap,
            Consumer<Mob> interruptedLeap,
            Consumer<Mob> endingRunnable
    ) {
        this.shouldStopCurrentLeap = shouldStopCurrentLeap;
        this.isOnGround = isOnGround;
        this.preLeap = preLeap;
        this.interruptedLeap = interruptedLeap;
        this.endLeap = endingRunnable;
    }

    public boolean shouldStopCurrentLeap(@Nullable LeapDo leapDo) {
        return shouldStopCurrentLeap.apply(leapDo);
    }

    public boolean isOnGround() {
        return isOnGround.getAsBoolean();
    }

    public void runEnd(Mob entity) {
        this.endLeap.accept(entity);
    }

    public void runInterrupted(Mob entity) {
        this.interruptedLeap.accept(entity);
    }

    public void runPreLeap(Mob entity, LeapDo leapDo) {
        this.preLeap.accept(entity, leapDo);
    }
}
