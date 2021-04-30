package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;

public enum LeapTypes {
    ;
//    TERRA2("terra_leap", LeapSpecificMisc::eatSpawnEvent),

    private final String leapName;
    private final LeapSpawnListener.CustomSpawnEater leapEater;

    LeapTypes(String leapName, LeapSpawnListener.CustomSpawnEater leapEater) {
        this.leapName = leapName;
        this.leapEater = leapEater;
    }

    public String getTypeName() {
        return leapName;
    }

    public LeapSpawnListener.CustomSpawnEater getLeapEater() {
        return leapEater;
    }

    public LeapConfig getLeapConfig() {
        return LeapConfigManager.get().getLeap(getTypeName());
    }
}
