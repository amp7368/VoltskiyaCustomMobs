package apple.voltskiya.custom_mobs.abilities.common.grenade;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.sound.PlaySound;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.minecraft.world.BlockCollideUtils;

public abstract class ThrowGrenade<Config> {


    public static final double ANGLE_SAME_DIRECTION = Math.toRadians(90);
    private static final double BOUNCE_DEGRADE = 0.8;
    protected final Config config;
    private final Random random = new Random();
    protected Item grenade;
    private int tick = 0;
    private int fuseDuration;

    public ThrowGrenade(Config config) {
        this.config = config;
    }

    public void start(Location startLocation, Vector velocity, int fuseDuration) {
        this.fuseDuration = fuseDuration;
        grenade = startLocation.getWorld().dropItem(startLocation, makeItem(), (item) -> {
            item.setVelocity(velocity);
            item.setCanPlayerPickup(false);
            item.setCanMobPickup(false);
            item.addScoreboardTag(TagConstants.CLEANUP_KILL);
        });
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick);
    }

    private void tick() {
        Vector velocity = grenade.getVelocity();
        BoundingBox box = grenade.getBoundingBox().expandDirectional(velocity.clone().multiply(1.01));
        if (velocity.isZero()) {
            finishTick();
            return;
        }
        World world = grenade.getWorld();
        Vector normal = BlockCollideUtils.checkBlockCollide(box, world);
        if (normal != null && normal.angle(velocity) > ANGLE_SAME_DIRECTION) {
            double oldVelocityMagnitude = velocity.length();
            double dot = velocity.dot(normal);
            Vector outVector = velocity.subtract(normal.multiply(2 * dot));
            Vector newVelocity = outVector.normalize().multiply(oldVelocityMagnitude * BOUNCE_DEGRADE);

            grenade.setVelocity(newVelocity);
            this.collideSound(oldVelocityMagnitude).play(grenade.getLocation());
        }
        this.finishTick();
    }

    protected PlaySound collideSound(double velocity) {
        return new PlaySound(SoundCategory.BLOCKS, Sound.ITEM_SHIELD_BLOCK, collideVolume(velocity), 1.5f);
    }

    protected float collideVolume(double velocity) {
        float MAX_VELOCITY = 3;
        float MAX_VOLUME = 1;
        return (float) Math.min(MAX_VELOCITY, velocity) / MAX_VELOCITY * MAX_VOLUME;
    }

    private void finishTick() {
        if (++tick < fuseDuration) {
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick, 1);
        } else {
            this.explode();
        }
    }

    protected void remove() {
        grenade.remove();
    }

    protected void explode() {
        Location center = this.getLocation();
        World world = center.getWorld();
        double radius = explosionRadius();
        Collection<Entity> entities = center.getNearbyEntities(radius, radius, radius);
        List<GrenadeEntityImpact> impacts = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            List<Location> possibleHits = randomLocations(entity);
            int count = 0;
            for (Location loc : possibleHits) {
                Vector difference = loc.subtract(center).toVector();
                RayTraceResult ray = world.rayTraceBlocks(center, difference, difference.length(), FluidCollisionMode.NEVER, true);
                if (ray == null) count++;
            }
            double distance = entity.getLocation().distance(center);
            double hitPercentage = (double) count / possibleHits.size();
            impacts.add(new GrenadeEntityImpact(entity, hitPercentage, distance));
        }
        this.explode(impacts);
        this.remove();
    }

    protected abstract void explode(List<GrenadeEntityImpact> impacts);

    protected abstract double explosionRadius();

    private ItemStack makeItem() {
        return new ItemStack(Material.OAK_LOG);
    }

    protected final List<Location> randomLocations(Entity entity) {
        BoundingBox entityBox = entity.getBoundingBox();
        World world = entity.getWorld();
        Vector min = entityBox.getMin();
        Vector max = entityBox.getMax();
        List<Location> locations = new ArrayList<>(10);
        for (int i = 0; i < 10; i++)
            locations.add(randomLoc(world, min, max));
        return locations;
    }

    private Location randomLoc(World world, Vector min, Vector max) {
        double x = random.nextDouble() * (max.getX() - min.getX()) + min.getX();
        double y = random.nextDouble() * (max.getY() - min.getY()) + min.getY();
        double z = random.nextDouble() * (max.getZ() - min.getZ()) + min.getZ();
        return new Location(world, x, y, z);
    }

    protected Location getLocation() {
        return grenade.getLocation();
    }

}
