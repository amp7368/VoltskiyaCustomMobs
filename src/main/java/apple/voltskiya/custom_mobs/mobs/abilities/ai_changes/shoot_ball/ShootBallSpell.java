package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.projectile.ProjectileParticleMissle;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShootBallSpell implements PathfinderGoalShootSpell.Spell {
    private static final double EXPLOSION_RADIUS = 3.5;
    private static final double DAMAGE_AMOUNT = 4;
    private static final VoltskiyaPlugin PLUGIN = VoltskiyaPlugin.get();
    private final ShootBallCaster shootBallCaster;
    private final ShootBallManager.ShootersType shootersType;
    private final double shotSpeed;
    private EntityLiving goalTarget;
    private State state;
    private Location predictedLocation;
    private Location lastViableShot = null;

    public ShootBallSpell(ShootBallCaster shootBallCaster, ShootBallManager.ShootersType shootersType) {
        this.shootBallCaster = shootBallCaster;
        this.shootersType = shootersType;
        this.shotSpeed = shootersType.getShotSpeed();
        this.state = State.CHARGE_UP;
    }

    @Override
    public void stateChoice() {
        switch (state) {
            case CHARGE_UP -> new ChargeUp(shootersType.getChargeUpTicks());
            case SHOOT -> new Shooting(shootersType.getShotsToTake(), shootersType.getTimeToShoot());
        }
    }

    private enum State {
        CHARGE_UP,
        SHOOT,
        FINISH
    }

    private class ChargeUp implements Runnable {
        private static final long TICK_PER_STEP = 6;
        private static final double LASER_STEP = .3;
        private static final int LOCATION_TRACKING_TICKS = 30;

        private final int ticksToCharge;

        private final List<Location> previousLocations = new ArrayList<>();
        private Location nowLocation;
        private int currentTick = 0;

        private int soundCountdownIndex = 0;
        private float soundCountdown;

        public ChargeUp(int ticksToCharge) {
            this.ticksToCharge = ticksToCharge;
            this.soundCountdown = Math.max(1, this.ticksToCharge / 8);
            goalTarget = shootBallCaster.getEntity().getGoalTarget();
            LivingEntity target;
            if (goalTarget == null || goalTarget.getRemovalReason() != null) target = null;
            else {
                if (goalTarget.getBukkitEntity() instanceof LivingEntity)
                    target = (LivingEntity) goalTarget.getBukkitEntity();
                else target = null;
            }

            this.previousLocations.add(lastViableShot = this.nowLocation = target == null ? null : target.getEyeLocation());
            run();
        }

        @Override
        public void run() {
            if (!shootBallCaster.getEntity().isAlive()) {
                return; // no need to do anything more. it's dead
            }
            // track the target
            LivingEntity target;
            if (goalTarget == null || goalTarget.getRemovalReason() != null) target = null;
            else {
                if (goalTarget.getBukkitEntity() instanceof LivingEntity)
                    target = (LivingEntity) goalTarget.getBukkitEntity();
                else target = null;
            }
            this.nowLocation = target == null ? null : target.getEyeLocation();
            if (this.nowLocation == null) {
                this.dealWithResult();
                return;
            }
            this.previousLocations.add(this.nowLocation);
            while (this.previousLocations.size() > LOCATION_TRACKING_TICKS) this.previousLocations.remove(0);
            if (currentTick % TICK_PER_STEP == 0) {

                // get the distance to the current target location
                final CraftEntity me = shootBallCaster.getEntity().getBukkitEntity();
                Location myLocation = me instanceof Mob ? ((Mob) me).getEyeLocation() : me.getLocation();
                Vector vectorToTarget = myLocation.toVector().subtract(this.nowLocation.toVector());
                double distanceToTarget = VectorUtils.magnitude(vectorToTarget);

                // predict where the target will be when our shot gets to them if we shot right now
                Vector targetMovement;
                if (this.currentTick == 0)
                    targetMovement = new Vector(0, 0, 0);
                else {
                    targetMovement = this.nowLocation.toVector().subtract(this.previousLocations.get(0).toVector()).multiply(1d / this.previousLocations.size());
                }
                predictedLocation = this.nowLocation.clone().add(targetMovement.multiply((distanceToTarget / shotSpeed)));
                final Vector direction = predictedLocation.clone().toVector().subtract(myLocation.toVector());
                @Nullable RayTraceResult rayTrace = myLocation.getWorld().rayTraceBlocks(myLocation, direction, VectorUtils.magnitude(direction), FluidCollisionMode.NEVER, true);
                if (rayTrace == null) {
                    lastViableShot = predictedLocation;
                }
                this.aimLaser(myLocation.clone(), lastViableShot, distanceToTarget);
            }
            this.laserSound();
            // if we finished charging, stop charging
            if (this.currentTick >= this.ticksToCharge) {
                this.dealWithResult();
                return;
            }
            this.currentTick++;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, this, 1);
        }

        private void dealWithResult() {
            state = State.SHOOT;
            predictedLocation = lastViableShot;
            stateChoice();
        }

        private void aimLaser(Location myLocation, Location predictedLocation, double distance) {
            final Vector difference = predictedLocation.toVector().subtract(myLocation.toVector());
            Vector directionVector = difference.normalize().multiply(LASER_STEP);
            for (double currentStep = 0; currentStep <= distance; currentStep += LASER_STEP) {
                myLocation.getWorld().spawnParticle(Particle.REDSTONE, myLocation, 0, new Particle.DustOptions(Color.fromRGB(115, 0, 0), 2));
                myLocation.add(directionVector);
            }
        }

        private void laserSound() {
            if (--this.soundCountdownIndex <= 0) {
                this.soundCountdownIndex = (int) (this.soundCountdown = (int) Math.max(this.soundCountdown / 1.05, 1));
                // get the distance to the current target location
                final CraftEntity me = shootBallCaster.getEntity().getBukkitEntity();
                Location myLocation = me instanceof Mob ? ((Mob) me).getEyeLocation() : me.getLocation();
                float pitch = .5f - this.soundCountdown / 10f + 1f;
                myLocation.getWorld().playSound(myLocation, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.HOSTILE, 10f, pitch);
            }
        }
    }

    private class Shooting implements Runnable {
        private static final int TICK_PER_STEP = 1;
        private final List<Integer> shootTicks;
        private final Random random = new Random();
        private int currentTick = 0;

        public Shooting(int shotsToTake, int timeToShoot) {
            this.shootTicks = new ArrayList<>();
            for (int i = 0; i < shotsToTake; i++) {
                shootTicks.add(random.nextInt(timeToShoot));
            }
            this.shootTicks.sort(Integer::compareTo);
            run();
        }

        @Override
        public void run() {
            while (!shootTicks.isEmpty() && shootTicks.get(0) <= currentTick) {
                shootTicks.remove(0);
                CraftEntity me = shootBallCaster.getEntity().getBukkitEntity();
                Location eyeLocation = me instanceof LivingEntity ? ((LivingEntity) me).getEyeLocation() : me.getLocation();

                if (predictedLocation == null) {
                    dealWithResult();
                    return;
                }
                Vector direction = predictedLocation.toVector().subtract(eyeLocation.toVector());
                Location locationToShootFrom = eyeLocation.clone().add(direction.clone().normalize());
                Location shootForLocation = locationToShootFrom.clone().add(direction.multiply(2));
                shootSound(locationToShootFrom);
                new ProjectileParticleMissle(
                        locationToShootFrom,
                        shootForLocation,
                        direction.normalize(),
                        Collections.singletonList(Particle.FLAME),
                        .13,
                        this::finishedShotCallback,
                        3
                );
            }


            // if we finished shooting, stop shooting
            if (shootTicks.isEmpty()) {
                this.dealWithResult();
                return;
            }
            this.currentTick += TICK_PER_STEP;
            Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, this, TICK_PER_STEP);

        }

        private void dealWithResult() {
            state = State.FINISH;
            stateChoice();
        }

        private void shootSound(Location location) {
            location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 2.25f, (float) (1.6f + random.nextDouble() * .3));
        }

        private void finishedShotCallback(Location location) {
            location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.4f, 1.8f);

            Collection<Entity> caughtEntities = location.getNearbyEntities(EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS);
            for (Entity caughtEntity : caughtEntities) {
                if (caughtEntity instanceof LivingEntity) {
                    ((LivingEntity) caughtEntity).damage(DAMAGE_AMOUNT);
                }
            }
        }
    }
}
