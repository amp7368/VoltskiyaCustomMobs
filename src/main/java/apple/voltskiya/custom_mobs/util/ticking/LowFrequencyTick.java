package apple.voltskiya.custom_mobs.util.ticking;

public class LowFrequencyTick extends TickGiverable {

    private static LowFrequencyTick instance;

    public LowFrequencyTick() {
        super(40);
        instance = this;
    }

    public static LowFrequencyTick get() {
        return instance;
    }
}
