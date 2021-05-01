package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.leaps.hellish_catalyst.LeapHellishCatalyst;
import apple.voltskiya.custom_mobs.leaps.revenant.LeapRevenant;
import apple.voltskiya.custom_mobs.leaps.upwards.LeapUpwards;

public enum LeapType {
    UPWARDS_LEAP("upwards_leap", LeapUpwards::eatSpawnEvent) ,
    REVENANT_LEAP("revenant_leap", LeapRevenant::eatSpawnEvent) ,
    HELLISH_CATALYST_LEAP("hellish_catalyst_leap", LeapHellishCatalyst::eatSpawnEvent) ;

    private final String leapName;
    private final LeapSpawnListener.CustomSpawnEater leapEater;

    LeapType(String leapName, LeapSpawnListener.CustomSpawnEater leapEater) {
        this.leapName = leapName;
        this.leapEater = leapEater;
    }

    public String getTypeName() {
        return leapName;
    }

    public LeapSpawnListener.CustomSpawnEater getLeapEater() {
        return leapEater;
    }

    public LeapPreConfig getLeapConfig() {
        return LeapConfigManager.get().getLeap(getTypeName());
    }
}
