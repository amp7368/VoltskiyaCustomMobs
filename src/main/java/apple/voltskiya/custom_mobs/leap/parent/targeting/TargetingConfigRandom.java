package apple.voltskiya.custom_mobs.leap.parent.targeting;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapMath;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Random;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class TargetingConfigRandom extends TargetingConfig {

    private transient final Random random = new Random();

    public TargetingConfigRandom() {
        super("random");
    }

    @Nullable
    @Override
    public Location findTarget(MMSpawned mob, LeapConfig config) {
        LeapMath math = config.leap.math();
        double randomDistance;
        synchronized (random) {
            randomDistance = math.rangeBounds() * random.nextDouble() + math.minRange();
        }
        return mob.getLocation().add(mob.getLocation().getDirection().multiply(randomDistance));
    }
}
