package apple.voltskiya.custom_mobs.util.ticking;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TickGiverable implements Tickable {

    private final int ticksPerTick;
    private int currentTick;

    private final Map<Long, Runnable> tickering = new HashMap<>();
    private long uid = 0;

    protected TickGiverable(int ticksPerTick) {
        this.ticksPerTick = ticksPerTick;
    }

    public boolean shouldTick() {
        return currentTick++ % ticksPerTick == 0;
    }

    @Override
    public void tick() {
        if (shouldTick()) {
            this.currentTick = 1;
            synchronized (this) {
                for (Runnable runMe : List.copyOf(this.tickering.values())) {
                    try {
                        runMe.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public long add(Runnable runnable) {
        synchronized (tickering) {
            long u = uid++;
            tickering.put(u, runnable);
            return u;
        }
    }

    public void remove(long uid) {
        synchronized (tickering) {
            tickering.remove(uid);
        }
    }

    public int getTickSpeed() {
        return ticksPerTick;
    }


}
