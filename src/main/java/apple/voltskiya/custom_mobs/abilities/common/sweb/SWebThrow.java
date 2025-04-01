package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.mc.utilities.item.material.MaterialUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SWebThrow {

    private final SWebConfig config;
    private final Location location;
    private final Vector direction;
    private final double targetDistance;
    private double distanceTraveled = 0;

    public SWebThrow(SWebConfig config, Location start, Location end) {
        this.config = config;
        this.location = start;
        this.direction = end.toVector().subtract(start.toVector());
        this.targetDistance = Math.min(direction.length(), config.maxProjectileDistance);
        this.direction.normalize().multiply(config.projectileVelocity / 20f);
        progressWebThrow();
    }

    private void progressWebThrow() {
        World world = location.getWorld();
        location.add(direction);

        distanceTraveled += direction.length();
        boolean hitImpassable = !MaterialUtils.isPassable(location.getBlock().getType());
        if (distanceTraveled > targetDistance || hitImpassable) {
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
        location.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0,
            new Particle.DustOptions(Color.fromRGB(0xFFFFFF), 1.4f));
    }
}
