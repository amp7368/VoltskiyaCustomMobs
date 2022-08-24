package apple.voltskiya.custom_mobs.util.ticking;

public class HighFrequencyTick extends TickGiverable {

    private static HighFrequencyTick instance;


    public HighFrequencyTick() {
        super(1);
        instance = this;
    }

    public static HighFrequencyTick get() {
        return instance;
    }
}
