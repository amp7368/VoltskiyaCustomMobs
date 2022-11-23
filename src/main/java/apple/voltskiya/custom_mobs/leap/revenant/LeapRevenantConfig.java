package apple.voltskiya.custom_mobs.leap.revenant;

import apple.voltskiya.custom_mobs.leap.parent.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class LeapRevenantConfig extends LeapConfig {

    public LeapRevenantConfigPeaks peaks = new LeapRevenantConfigPeaks();

    public LeapRevenantConfig(String tag) {
        super(tag);
    }

    public LeapRevenantConfig() {
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapRevenantAbility<>(mob, this);
    }

    public static class LeapRevenantConfigPeaks {

        public int radiusToScan = 15;
        public int belowTargetY = -7;
        public int preferOutsideRadius = 5;
    }
}
