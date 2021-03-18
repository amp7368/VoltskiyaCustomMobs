package apple.voltskiya.custom_mobs.heartbeat.tick.main;

import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NormalFrequencyTick implements Tickable, TickGiverable {
    private static final int TICKS_PER_TICK = 10;
    private int currentTick = 0;


    private static NormalFrequencyTick instance;

    private final Map<Long, Runnable> tickering = new HashMap<>();
    private long uid = 0;

    public NormalFrequencyTick() {
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

    public static NormalFrequencyTick get() {
        return instance;
    }
}
