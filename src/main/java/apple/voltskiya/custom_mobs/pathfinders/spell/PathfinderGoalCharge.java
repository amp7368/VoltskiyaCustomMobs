package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PathfinderGoalCharge extends PathfinderGoal {
    private static final int MAX_LOCATION_TRACKING = 20;
    private final double minSpeed;
    private final Location target;
    private final Collection<ChargeResult> bothOn;
    private final EntityInsentient me;
    private final int giveUpTick;
    private final Consumer<ChargeResult> callBack;
    private final double speed;
    private boolean calledBack = false;
    private ChargeResult chargeResult = null;
    private final CraftEntity bukkitEntity;
    private final List<Location> lastLocations = new ArrayList<>();
    private boolean isCircling = false;

    public PathfinderGoalCharge(EntityInsentient me, Location target, double speed, int giveUpTick, Collection<ChargeResult> bothOn, Consumer<ChargeResult> callBack) {
        this.me = me;
        this.target = target.clone();
        this.bothOn = bothOn;
        this.giveUpTick = DecodeEntity.getTicksLived(me) + giveUpTick;
        this.callBack = callBack;
        this.speed = speed;
        this.minSpeed = speed / 5000;
        this.bukkitEntity = this.me.getBukkitEntity();
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        ChargeResult chargeResultTemp = null;
        if (DecodeEntity.getTicksLived(me) > this.giveUpTick) {
            chargeResultTemp = ChargeResult.HIT_NOTHING;
        } else {
            Location here = this.bukkitEntity.getLocation();
            if (DistanceUtils.distance(here, this.target) <= 1.5) {
                chargeResultTemp = ChargeResult.HIT_NOTHING;
            } else {
                final World world = here.getWorld();
                here.add(this.target.clone().subtract(here).toVector().setY(0).normalize());
                if (!MaterialUtils.isWalkThroughable(world.getBlockAt(here).getType()) && !MaterialUtils.isWalkThroughable(world.getBlockAt(here.add(0, 1, 0)).getType())) {
                    chargeResultTemp = ChargeResult.HIT_WALL;
                } else {
                    final List<Entity> nearbyEntities = this.bukkitEntity.getNearbyEntities(2d, 2d, 2d);
                    for (Entity nearby : nearbyEntities) {
                        if (nearby != this.bukkitEntity && nearby instanceof LivingEntity && !(nearby instanceof ArmorStand)) {
                            chargeResultTemp = ChargeResult.HIT_ENTITY;
                            break;
                        }
                    }
                }
            }
        }
        if (chargeResultTemp != null) chargeResult = chargeResultTemp;
        // now we have a chargeResult
        if (bothOn.contains(chargeResultTemp)) {
            callBack.accept(chargeResultTemp);
            return !this.isCircling;
        } else return chargeResultTemp == null && !this.isCircling;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return this.a();
    }

    /**
     * @return something
     */
    @Override
    public boolean C_() {
        return true;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void c() {
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        if (chargeResult == null) chargeResult = ChargeResult.HIT_NOTHING;
        // quit going to the location
        this.me.getNavigation().o();
        if (!calledBack) {
            try {
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> callBack.accept(chargeResult));
            } catch (IllegalPluginAccessException ignored) {
            }
            calledBack = true;
        }
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> DecodeEntity.getGoalSelector(me).a(this));
        } catch (IllegalPluginAccessException ignored) {
        }
    }

    /**
     * run the pathfinding
     */
    @Override
    public void e() {
        Location here = this.bukkitEntity.getLocation();
        this.lastLocations.add(here);
        while (this.lastLocations.size() > MAX_LOCATION_TRACKING) this.lastLocations.remove(0);
        if (this.lastLocations.size() == MAX_LOCATION_TRACKING && DistanceUtils.distance(this.lastLocations.get(0), here) < minSpeed * MAX_LOCATION_TRACKING) {
            this.isCircling = true;
        }
        here = here.clone();
        // go to the location
        Vector direction = this.target.clone().subtract(here).toVector().setY(0).normalize().multiply(0.9);
        here.setDirection(direction);
        if (!MaterialUtils.isWalkThroughable(here.getWorld().getBlockAt(here.add(direction)).getType())) {
            this.me.getControllerJump().jump();
        }
        this.me.setYRot(0f);
        this.me.setXRot(here.getYaw());
        this.me.getNavigation().o();
        this.me.getControllerMove().a(me.locX() + direction.getX(), me.locY() + direction.getY(), me.locZ() + direction.getZ(), speed);
    }

    public enum ChargeResult {
        HIT_ENTITY,
        HIT_WALL,
        HIT_NOTHING
    }
}