package apple.voltskiya.custom_mobs.jumps;

import apple.voltskiya.custom_mobs.util.BinaryTree;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeapMob {
    private Entity mob;
    private long lastLeaping = 0;
    private boolean isCurrentlyLeaping = false;
    private Map<String, Object> tags = new HashMap<>();
    private final List<LeapMobListenerManager> listeners = new ArrayList<>();
    private final List<LeapMobListenerManager> disabledListeners = new ArrayList<>();
    private final BinaryTree<Long> nextCall = new BinaryTree<Long>();

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
