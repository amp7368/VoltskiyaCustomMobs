package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredEntityEater;

public interface LeapEater extends RegisteredEntityEater {
    default LeapPreConfig getConfig() {
        return LeapConfigManager.getLeap(getName());
    }
}
