package apple.voltskiya.custom_mobs.jumps.tick.small_leap;

import apple.voltskiya.custom_mobs.jumps.LeapMob;
import apple.voltskiya.custom_mobs.jumps.LeapMobListenerManager;

public class LeapSmallMobListener implements LeapMobListenerManager {
    public static LeapSmallMobListener instance;

    public LeapSmallMobListener() {
        instance = this;
    }

    public static LeapSmallMobListener get() {
        return instance;
    }

    @Override
    public void eatLeapMob(LeapMob leapMob) {

    }
}
