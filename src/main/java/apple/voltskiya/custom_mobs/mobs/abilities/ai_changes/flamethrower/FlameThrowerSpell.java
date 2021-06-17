package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;

public class FlameThrowerSpell implements PathfinderGoalShootSpell.Spell {
    public static final VoltskiyaPlugin PLUGIN = VoltskiyaPlugin.get();
    private State state;
    private final EntityInsentient me;
    private final FlameThrowerManager.FlamethrowerType flamethrowerType;
    private final FlameThrowerShot flameThrower;


    public FlameThrowerSpell(FlameThrowerCaster me, FlameThrowerManager.FlamethrowerType type) {
        this.me = me.getEntity();
        this.flamethrowerType = type;
        this.state = FlameThrowerSpell.State.CHARGE_UP;
        this.flameThrower = new FlameThrowerShot(me, 3, 45);
    }

    @Override
    public void stateChoice() {
        switch (state) {
            case CHARGE_UP:
                new ChargeUp(30).run();
            case FIRE_GROW:
                new ShootGrow(40, 8).run();
            case FIRE_PERSIST:
                new ShootFire(80).run();
        }
    }

    public enum State {
        FIRE_GROW,
        FIRE_PERSIST,
        COMPLETE,
        CHARGE_UP
    }


    private class ChargeUp implements Runnable {
        private static final int TICK_PER_STEP = 5;
        private int count = 0;
        private final int chargeUpTicks;

        private ChargeUp(int chargeUpTicks) {
            this.chargeUpTicks = chargeUpTicks;
        }

        @Override
        public void run() {
            if (this.count >= this.chargeUpTicks) {
                dealWithResult();
                return;
            }
            this.count += TICK_PER_STEP;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, this, TICK_PER_STEP);
        }

        private void dealWithResult() {
            state = State.FIRE_GROW;
            stateChoice();
        }
    }

    private class ShootGrow implements Runnable {
        private static final int TICK_PER_STEP = 1;
        private final double startLength;
        private int tick = 0;
        private final int ticksToGrow;
        private final int finalLength;

        private ShootGrow(int ticksToGrow, int finalLength) {
            this.ticksToGrow = ticksToGrow;
            this.startLength = flameThrower.getLength();
            this.finalLength = finalLength;
        }

        @Override
        public void run() {
            if (this.tick >= this.ticksToGrow) {
                dealWithResult();
                return;
            }
            flameThrower.shoot();
            flameThrower.setLength(this.grow());
            this.tick += TICK_PER_STEP;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, this, TICK_PER_STEP);
        }

        private double grow() {
            return ((double) tick) / ticksToGrow * (finalLength - startLength) + startLength;
        }

        private void dealWithResult() {
            state = State.FIRE_PERSIST;
            stateChoice();
        }
    }

    private class ShootFire implements Runnable {
        private static final int TICK_PER_STEP = 1;
        private final int ticksToLive;
        private int tick = 0;

        public ShootFire(int duration) {
            this.ticksToLive = duration;
        }

        @Override
        public void run() {
            if (this.tick >= this.ticksToLive) {
                dealWithResult();
                return;
            }
            flameThrower.shoot();
            this.tick += TICK_PER_STEP;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, this, TICK_PER_STEP);
        }

        private void dealWithResult() {
            state = State.COMPLETE;
            stateChoice();
        }
    }
}
