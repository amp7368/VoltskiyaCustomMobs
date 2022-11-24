package apple.voltskiya.custom_mobs.leap.parent.config;

public class LeapMoveConfig {

    double gravity = -0.1;
    double maxPeakHeight = 10;
    double minPeakHeight = -1;
    double maxRange = 10;
    double minRange = -1;
    double preferredPeakHeight = -1;
    private transient LeapMath math = null;

    public LeapMoveConfig() {
    }

    public LeapMath math() {
        if (this.math != null)
            return this.math;
        return this.math = new LeapMath(this);
    }
}
