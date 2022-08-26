package apple.voltskiya.custom_mobs.util.ticking;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;

public class NormalFrequencyTick extends TickGiverable implements Tickable {


    private static NormalFrequencyTick instance;


    public NormalFrequencyTick() {
        super(10);
        instance = this;
    }


    public static NormalFrequencyTick get() {
        return instance;
    }
}
