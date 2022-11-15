package apple.voltskiya.custom_mobs.util.ticking;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;

public class NormalHighFrequencyTick extends TickGiverable implements Tickable {

    private static NormalHighFrequencyTick instance;

    public NormalHighFrequencyTick() {
        super(5);
        instance = this;
    }


    public static NormalHighFrequencyTick get() {
        return instance;
    }
}
