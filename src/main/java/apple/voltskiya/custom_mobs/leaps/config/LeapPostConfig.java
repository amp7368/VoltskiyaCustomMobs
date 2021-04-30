package apple.voltskiya.custom_mobs.leaps.config;

import net.minecraft.server.v1_16_R3.EntityInsentient;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class LeapPostConfig {
    private final BooleanSupplier shouldStopCurrentLeap;
    private final BooleanSupplier isOnGround;
    private final BiConsumer<EntityInsentient, Runnable> preLeap;
    private final Consumer<EntityInsentient> interruptedLeap;
    private final Consumer<EntityInsentient> endLeap;

    public LeapPostConfig(
            BooleanSupplier shouldStopCurrentLeap,
            BooleanSupplier isOnGround,
            BiConsumer<EntityInsentient, Runnable> preLeap,
            Consumer<EntityInsentient> interruptedLeap,
            Consumer<EntityInsentient> endingRunnable
    ) {
        this.shouldStopCurrentLeap = shouldStopCurrentLeap;
        this.isOnGround = isOnGround;
        this.preLeap = preLeap;
        this.interruptedLeap = interruptedLeap;
        this.endLeap = endingRunnable;
    }

    public boolean shouldStopCurrentLeap() {
        return shouldStopCurrentLeap.getAsBoolean();
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

    public void runPreLeap(EntityInsentient entity, Runnable leap) {
        this.preLeap.accept(entity, leap);
    }
}
