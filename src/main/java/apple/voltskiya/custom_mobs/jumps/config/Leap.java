package apple.voltskiya.custom_mobs.jumps.config;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Leap implements Runnable {
    private final Entity entity;
    private final Location goalLocation;
    private final int peakHeight;
    private final int timeFullArc;
    private final int timeFinal;
    private final double xVelocity;
    private double yVelocity;
    private final double zVelocity;
    private final double yVelocityInitial;
    private int currentTime = 0;
    private double acceleration;

    public Leap(Entity entity, Location goalLocation, int peakHeight, int timeFullArc) {
        this.entity = entity;
        this.goalLocation = goalLocation;
        this.peakHeight = peakHeight;
        this.timeFullArc = timeFullArc;
        Location nowLocation = entity.getLocation();
        double xDistance = goalLocation.getX() - nowLocation.getX();
        double yDistance = goalLocation.getY() - nowLocation.getY();
        double zDistance = goalLocation.getZ() - nowLocation.getZ();
        this.xVelocity = xDistance / timeFullArc;
        this.zVelocity = zDistance / timeFullArc;
        // how much time does it take to reach the peak?
        //
        // xf = xi + (vi-vf)(t)/2
        // peakHeight = (vi)(t)/2
        // peakHeight/t*2 = vi`
        this.yVelocityInitial = this.yVelocity = ((double) peakHeight) / timeFullArc * 4;
        // vf = vi + at/2
        // a = (vf - vi)/t/2
        this.acceleration = -this.yVelocityInitial / timeFullArc * 2;

        this.timeFinal = timeFullArc;
        leap();
    }

    private void leap() {
        entity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        run();
    }

    @Override
    public void run() {
        if (entity.isDead() || ++currentTime == timeFullArc) return;
        // set the yVelocity to what it should
        yVelocity = yVelocityInitial + acceleration * currentTime;
        entity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));

        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
    }
}
