package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SWebThrow {

    private static final double MAX_DISTANCE = 16;
    private static final double SPEED = 8 / 20d;
    private final SWebConfig config;
    private final Location location;
    private final Location end;
    private final Vector direction;
    private double distanceTraveled = 0;
    private final double targetDistance;

    public SWebThrow(SWebConfig config, Location start, Location end) {
        this.config = config;
        this.location = start;
        this.end = end;
        this.direction = end.toVector().subtract(start.toVector());
        this.targetDistance = Math.min(direction.length(), MAX_DISTANCE);
        this.direction.normalize().multiply(SPEED);
        progressWebThrow();
    }

    private void progressWebThrow() {
        World world = location.getWorld();
        location.add(direction);

        distanceTraveled += direction.length();
        if (distanceTraveled > targetDistance) {
            SWebEffect effect = new SWebEffect(config, location.subtract(0, 0.1, 0));
            effect.start();
            return;
        }
        if (location.getY() < world.getMinHeight())
            return;
        if (location.getY() > world.getMaxHeight())
            return;

        particles();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::progressWebThrow, 1);
    }

    private void particles() {
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0,
            new Particle.DustOptions(Color.fromRGB(0xFFFFFF), 1.4f));
    }
}
