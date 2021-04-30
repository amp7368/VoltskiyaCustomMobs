package apple.voltskiya.custom_mobs.leaps;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeapMob {
    private final Entity mob;
    private final long lastLeaping = 0;
    private final boolean isCurrentlyLeaping = false;
    private final Map<String, Object> tags = new HashMap<>();
    private final List<LeapMobListenerManager> listeners = new ArrayList<>();
    private final List<LeapMobListenerManager> disabledListeners = new ArrayList<>();

    public LeapMob(Entity mob) {
        this.mob = mob;
    }

    public void addManager(LeapMobListenerManager eaterManager) {
    }

    public void tick() {
        for (LeapMobListenerManager listener : listeners) {
            listener.eatLeapMob(this);
        }
    }
}
