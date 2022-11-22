package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;

@FunctionalInterface
public interface CreateLeapStage<Config extends LeapConfig> {

    LeapStage<Config> create(MMSpawned mob, Config config, Location target);
}
