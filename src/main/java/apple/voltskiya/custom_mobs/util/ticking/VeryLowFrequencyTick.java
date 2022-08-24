package apple.voltskiya.custom_mobs.util.ticking;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;

public class VeryLowFrequencyTick extends TickGiverable implements Tickable {

    private static VeryLowFrequencyTick instance;

    public VeryLowFrequencyTick() {
        super(100);
        instance = this;
    }

    public static VeryLowFrequencyTick get() {
        return instance;
    }
}
