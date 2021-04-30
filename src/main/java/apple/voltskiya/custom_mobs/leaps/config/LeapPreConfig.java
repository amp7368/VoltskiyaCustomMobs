package apple.voltskiya.custom_mobs.leaps.config;

import org.bukkit.Location;

public class LeapPreConfig {
    private final double timeFullArc;
    private final double peak;
    private final double distanceMin;
    private final double distanceMax;
    private final int checkInterval;
    private double currentPeak;

    public LeapPreConfig(double timeFullArc, double peak, double distanceMin, double distanceMax, int checkInterval) {
        this.timeFullArc = timeFullArc;
        this.peak = peak;
        this.distanceMin = distanceMin;
        this.distanceMax = distanceMax;
        this.checkInterval = checkInterval;
    }

    public double getTimeFullArc() {
        return this.timeFullArc;
    }

    public double getPeak() {
        return this.peak;
    }

    public double getDistanceMin() {
        return this.distanceMin;
    }

    public double getDistanceMax() {
        return this.distanceMax;
    }

    public int getCheckInterval() {
        return this.checkInterval;
    }

    public boolean isCorrectRange(Location me, Location them) {
        double xDistance = them.getX() - me.getX();
        double zDistance = them.getZ() - me.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);
        double yDistance = them.getY() - me.getY();
        return yDistance <= this.getPeak() && xzDistance > distanceMin && xzDistance < distanceMax;
    }


    public void randomizePeak() {
        this.currentPeak = this.peak * Math.random() * .4 + .8;
    }
}
