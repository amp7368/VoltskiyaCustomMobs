package apple.voltskiya.custom_mobs.jumps;

import apple.voltskiya.custom_mobs.jumps.tick.LeapListenerTemp;
import apple.voltskiya.custom_mobs.jumps.tick.small_leap.LeapSmallMobListener;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

public class LeapMobManager {
    private static LeapMobManager instance;

    // this is gonna be a long list of leap mobs
    private final HashMap<UUID, LeapMob> leapMobs = new HashMap<>();

    public LeapMobManager() {
        instance = this;
    }

    public static void register(LivingEntity entity, LeapMobListenerManager leapSmallMobListener) {
        get().leapMobs.compute(entity.getUniqueId(), ((uuid, leapMob) -> {
            if (leapMob == null) leapMob = new LeapMob(entity);
//            leapMob.addManager();
            return leapMob;
        }));
    }

    private static LeapMobManager get() {
        return instance;
    }
}
