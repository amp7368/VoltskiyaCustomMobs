package apple.voltskiya.custom_mobs.leaps.config;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LeapPreConfig {
    private final double timeFullArc;
    private final double peak;
    private final double distanceMin;
    private final double distanceMax;
    private final int checkInterval;
    private double currentPeak;
    private final int cooldown;

    public LeapPreConfig(double timeFullArc, double peak, double distanceMin, double distanceMax, int checkInterval, int cooldown) {
        this.timeFullArc = timeFullArc;
        this.currentPeak = this.peak = peak;
        this.distanceMin = distanceMin;
        this.distanceMax = distanceMax;
        this.checkInterval = checkInterval;
        this.cooldown = cooldown;
    }

    public double getTimeFullArc() {
        return this.timeFullArc;
    }

    public double getPeak() {
        return this.currentPeak;
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

    public int getCooldown() {
        return this.cooldown;
    }

    public boolean isCorrectRange(@NotNull Location me, @NotNull Location them) {
        double xDistance = them.getX() - me.getX();
        double zDistance = them.getZ() - me.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);
        return xzDistance > distanceMin && xzDistance < distanceMax;
    }

    public boolean isValidPeak(Location me, Location them) {
        double yDistance = them.getY() - me.getY();
        return yDistance <= this.getPeak();
    }

    public void randomizePeak() {
        this.currentPeak = this.peak * (Math.random() * .4 + .8);
    }

    public void correctPeak(@NotNull Location me, @NotNull Location them, double hitBoxHeight) {
        // find the middle
        double xMid = (me.getX() + them.getX()) / 2;
        double yMax = Math.max(me.getY(), them.getY());
        double yCheckMe = yMax + getPeak();
        double zMid = (me.getZ() + them.getZ()) / 2;
        final double peak = this.getPeak();
        // while it's not air, go down as far as reasonable
        boolean isCeiling = false;
        int blocksFree = 0;
        while (yCheckMe >= yMax) {
            if (!me.getWorld().getBlockAt((int) xMid, (int) yCheckMe, (int) zMid).getType().isAir()) {
                isCeiling = true;
                blocksFree = 0;
            } else {
                blocksFree++;
                if (blocksFree >= hitBoxHeight) {
                    break;
                }
            }
            yCheckMe--;
        }
        if (isCeiling) {
            yCheckMe -= hitBoxHeight;
        }
        double newMaxPeak = yCheckMe - yMax;

        this.currentPeak = Math.min(newMaxPeak, peak);
    }
}
