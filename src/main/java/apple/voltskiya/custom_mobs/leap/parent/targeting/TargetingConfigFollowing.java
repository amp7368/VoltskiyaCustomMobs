package apple.voltskiya.custom_mobs.leap.parent.targeting;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class TargetingConfigFollowing extends TargetingConfig {

    public boolean isEyeLocation = false;

    public TargetingConfigFollowing() {
        super("following");
    }

    public Location findTarget(MMSpawned mob, LeapConfig config) {
        if (!mob.isMob())
            return null;
        LivingEntity target = mob.getTarget();
        if (target == null)
            return null;
        return this.isEyeLocation ? target.getEyeLocation() : target.getLocation();
    }
}
