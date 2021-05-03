package apple.voltskiya.custom_mobs.ticking;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HighFrequencyTick implements Tickable, TickGiverable {
    private static final int TICKS_PER_TICK = 1;
    private static HighFrequencyTick instance;

    private final Map<Long, Runnable> tickering = new HashMap<>();
    private long uid = 0;

    public HighFrequencyTick() {
        instance = this;
    }

    @Override
    public void tick() {
        synchronized (tickering) {
            for (Runnable runMe : new ArrayList<>(tickering.values())) {
                try {
                    runMe.run();
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static HighFrequencyTick get() {
        return instance;
    }
}
