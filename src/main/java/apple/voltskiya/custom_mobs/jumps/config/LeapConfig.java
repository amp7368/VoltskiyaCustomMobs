package apple.voltskiya.custom_mobs.jumps.config;

public class LeapConfig {

    private final int leapTime;
    private final double leapPeak;
    private final int minDistance;
    private final int maxDistance;

    public LeapConfig(int leapTime, double leapPeak, int minDistance, int maxDistance) {
        this.leapTime = leapTime;
        this.leapPeak = leapPeak;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }
}
