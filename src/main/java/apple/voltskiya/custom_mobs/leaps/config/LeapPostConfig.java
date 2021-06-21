package apple.voltskiya.custom_mobs.leaps.config;

import net.minecraft.world.entity.EntityInsentient;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class LeapPostConfig {
    private final Function<LeapDo, Boolean> shouldStopCurrentLeap;
    private final BooleanSupplier isOnGround;
    private final BiConsumer<EntityInsentient, LeapDo> preLeap;
    private final Consumer<EntityInsentient> interruptedLeap;
    private final Consumer<EntityInsentient> endLeap;

    public LeapPostConfig(
            Function<LeapDo, Boolean> shouldStopCurrentLeap,
            BooleanSupplier isOnGround,
            BiConsumer<EntityInsentient, LeapDo> preLeap,
            Consumer<EntityInsentient> interruptedLeap,
            Consumer<EntityInsentient> endingRunnable
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

    public void runEnd(EntityInsentient entity) {
        this.endLeap.accept(entity);
    }

    public void runInterrupted(EntityInsentient entity) {
        this.interruptedLeap.accept(entity);
    }

    public void runPreLeap(EntityInsentient entity, LeapDo leapDo) {
        this.preLeap.accept(entity, leapDo);
    }
}
