package apple.voltskiya.custom_mobs.leap.parent.targeting;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;

public abstract class TargetingConfig {

    public String typeId;

    public TargetingConfig(String typeId) {
        this.typeId = typeId;
    }

    public abstract Location findTarget(MMSpawned mob, LeapConfig config);
}
