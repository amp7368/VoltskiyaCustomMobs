package apple.voltskiya.custom_mobs.abilities.nether.warper;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class MobWarper extends MMAbility<WarperConfig> {

    public MobWarper(MMSpawned mob, WarperConfig warperConfig) {
        super(mob, warperConfig);
    }

    @Override
    public void startAbility() {
        @Nullable LivingEntity target = getTarget();
        if (target == null) {
            finishAbility();
            return;
        }
        Location warpTo = findWarpTo(target.getLocation());
        if (warpTo != null) {
            particles(getLocation(), warpTo);
            getEntity().teleport(warpTo, PlayerTeleportEvent.TeleportCause.COMMAND);
            getMob().setTarget(target);
        }
        finishAbility();
    }

    @Override
    protected boolean canStartAbility() {
        return hasTarget();
    }

    private synchronized void particles(Location now, Location to) {
        double x = now.getX();
        double y = now.getY();
        double z = now.getZ();
        double xf = to.getX();
        double yf = to.getY();
        double zf = to.getZ();
        int particles = Math.max(config.particles, 1);
        double xInterval = (xf - x) / particles;
        double yInterval = (yf - y) / particles;
        double zInterval = (zf - z) / particles;
        World world = now.getWorld();
        for (int i = 0; i < particles; i++, x += xInterval, y += yInterval, z += zInterval) {
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, x, y, z, 0);
        }
    }

    @Nullable
    private synchronized Location findWarpTo(Location targetLocation) {
        double theta = random.nextDouble() * 360;
        double thetay = random.nextDouble() * 360;
        double radius = random.nextDouble() * config.range.maxRange;
        double x = Math.cos(Math.toRadians(theta)) * radius + targetLocation.getX();
        double y = Math.sin(Math.toRadians(theta)) * radius + targetLocation.getY();
        double z = Math.sin(Math.toRadians(thetay)) * radius + targetLocation.getZ();
        World world = targetLocation.getWorld();
        Location location = new Location(world, x, y, z);
        if (location.getBlock().getType().isAir() && location.clone().add(0, 1, 0).getBlock().getType().isAir())
            return location;
        return null;
    }

    @Override
    public void cleanUp(boolean b) {

    }

}

