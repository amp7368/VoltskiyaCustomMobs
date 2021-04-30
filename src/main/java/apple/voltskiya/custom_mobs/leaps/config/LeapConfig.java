package apple.voltskiya.custom_mobs.leaps.config;

import org.bukkit.Location;

public class LeapConfig {
    private final double timeFullArc;
    private final double peak;
    private final double distanceMin;
    private final double distanceMax;
    private final int checkInterval;

    public LeapConfig(double timeFullArc, double peak, double distanceMin, double distanceMax, int checkInterval) {
        this.timeFullArc = timeFullArc;
        this.peak = peak;
        this.distanceMin = distanceMin;
        this.distanceMax = distanceMax;
        this.checkInterval = checkInterval;
    }

    public double getTimeFullArc() {
        return timeFullArc;
    }

    public double getPeak() {
        return peak;
    }

    public double getDistanceMin() {
        return distanceMin;
    }

    public double getDistanceMax() {
        return distanceMax;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public boolean isCorrectRange(Location me, Location them) {
        double xDistance = them.getX() - me.getX();
        double zDistance = them.getZ() - me.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);
        double yDistance = them.getY() - me.getY();
        return yDistance <= peak && xzDistance > distanceMin && xzDistance < distanceMax;
    }


}
