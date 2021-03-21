package apple.voltskiya.custom_mobs.ticking;

import apple.voltskiya.custom_mobs.mob_tick.tick.Tickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LowFrequencyTick implements Tickable, TickGiverable {
    private static final int TICKS_PER_TICK = 40;
    private int currentTick;
    private static LowFrequencyTick instance;

    private final Map<Long, Runnable> tickering = new HashMap<>();
    private long uid = 0;

    public LowFrequencyTick() {
        instance = this;
    }

    @Override
    public void tick() {
        if (currentTick++ % TICKS_PER_TICK == 0) {
            currentTick = 1;
            synchronized (tickering) {
                for (Runnable runMe : new ArrayList<>(tickering.values())) {
                    runMe.run();
                }
            }
        }
    }

    @Override
    public long add(Runnable runnable) {
        synchronized (tickering) {
            long u = uid++;
            tickering.put(u, runnable);
            return u;
        }
    }

    @Override
    public void remove(long uid) {
        synchronized (tickering) {
            tickering.remove(uid);
        }
    }

    @Override
    public int getTickSpeed() {
        return TICKS_PER_TICK;
    }

    public static LowFrequencyTick get() {
        return instance;
    }
}
