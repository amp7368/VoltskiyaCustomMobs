package apple.voltskiya.custom_mobs.abilities.tick.warper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WarperIndividualTicker {
    private final WarperManagerTicker.Closeness closeness;
    private boolean isWarping = false;
    private final ArrayList<UUID> warpers = new ArrayList<>();
    private boolean isTicking = false;
    private final Random random = new Random();
    private long tickering;

    public WarperIndividualTicker(WarperManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveWarper(Entity warper) {
        this.warpers.add(warper.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.tickering = closeness.getGiver().add(this::tick);
        }
    }

    private synchronized void tick() {
        boolean trim = false;
        Iterator<UUID> warperIterator = warpers.iterator();
        while (warperIterator.hasNext()) {
            UUID warperUuid = warperIterator.next();
            @Nullable Entity warper = Bukkit.getEntity(warperUuid);

            if (warper == null || warper.isDead()) {
                warperIterator.remove();
                trim = true;
                continue;
            }
            tickWarper(warper);
            if (WarperManagerTicker.get().amIGivingWarper(warper, closeness)) {
                warperIterator.remove();
                trim = true;
            }
        }
        if (trim) {
            if (warpers.isEmpty()) {
                closeness.getGiver().remove(tickering);
                this.isTicking = false;
            }
            warpers.trimToSize();
        }
    }

    private synchronized void tickWarper(Entity warper) {
        if (isWarping) {
            if (random.nextDouble() < WarperManagerTicker.get().WARP_CHANCE * closeness.getGiver().getTickSpeed()) {
                warp(warper);
            }
        }
    }

    private synchronized void warp(Entity warper) {
        if (warper instanceof Mob) {
            Mob mob = (Mob) warper;
            @Nullable LivingEntity target = mob.getTarget();
            if (target == null) {
            } else {
                Location warpTo = null;
                for (int i = 0; i < 10; i++) {
                    warpTo = findWarpTo(target.getLocation());
                    if (warpTo != null) break;
                }
                if (warpTo != null) {
                    particles(warper.getLocation(), warpTo);
                    mob.teleport(warpTo, PlayerTeleportEvent.TeleportCause.COMMAND);
                    mob.setTarget(target);
                }
            }
        }
    }

    private synchronized void particles(Location now, Location to) {
        double x = now.getX();
        double y = now.getY();
        double z = now.getZ();
        double xf = to.getX();
        double yf = to.getY();
        double zf = to.getZ();
        final int particles = WarperManagerTicker.get().PARTICLES;
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
        double radius = random.nextDouble() * WarperManagerTicker.get().WARP_RADIUS;
        double x = Math.cos(Math.toRadians(theta)) * radius + targetLocation.getX();
        double y = Math.sin(Math.toRadians(theta)) * radius + targetLocation.getY();
        double z = Math.sin(Math.toRadians(thetay)) * radius + targetLocation.getZ();
        World world = targetLocation.getWorld();
        if (world.getBlockAt((int) x, (int) y, (int) z).getType().isAir() &&
                world.getBlockAt((int) x, (int) y + 1, (int) z).getType().isAir()) {
            return new Location(world, x, y, z);
        }
        return null;
    }

    public void setIsWarping() {
        this.isWarping = true;
    }
}
