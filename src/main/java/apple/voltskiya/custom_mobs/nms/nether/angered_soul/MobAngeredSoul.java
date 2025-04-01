package apple.voltskiya.custom_mobs.nms.nether.angered_soul;

import apple.voltskiya.custom_mobs.nms.base.INmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMobSupers;
import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalApproachSlowly;
import apple.voltskiya.custom_mobs.pathfinders.target_selector.PathfinderGoalClosestPlayer;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class MobAngeredSoul extends Skeleton implements INmsMob<MobAngeredSoul> {

    public static final double EXPLOSION_RADIUS = 1.5;
    private static final double SIGHT = 100;
    private static final double ANGLE_TO_BLOCK = Math.toRadians(45);
    private static AngeredSoulSpawner spawner;
    private NmsMob<MobAngeredSoul> wrapper;


    public MobAngeredSoul(EntityType<Skeleton> type, Level level) {
        super(type, level);
    }

    public static NmsSpawner<MobAngeredSoul, ?> spawner() {
        if (spawner != null) return spawner;
        return spawner = new AngeredSoulSpawner();
    }

    @Override
    public MobAngeredSoul getSelf() {
        return this;
    }

    @Override
    public NmsMob<MobAngeredSoul> wrapper() {
        if (this.wrapper != null) return this.wrapper;
        return this.wrapper = createWrapper();
    }

    @Override
    public NmsSpawner<MobAngeredSoul, ?> getInstSpawner() {
        return spawner();
    }


    @Override
    public NmsMobSupers<MobAngeredSoul> makeEntitySupers() {
        return new NmsMobSupers<>(
            super::teleport,
            super::move,
            super::load,
            super::save,
            super::saveWithoutId,
            super::remove
        );
    }

    @Override
    public void prepare() {
        this.setNoGravity(true);
        this.getBukkitEntity().addScoreboardTag(TagConstants.NO_FALL_DAMAGE);
    }

    public void explode() {
        List<org.bukkit.entity.Entity> nearbyEntities = this.getBukkitEntity()
            .getNearbyEntities(EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS);
        for (org.bukkit.entity.Entity nearby : nearbyEntities) {
            if (nearby instanceof org.bukkit.entity.LivingEntity living) {
                LivingEntity handle = ((CraftLivingEntity) nearby).getHandle();
                if (nearby instanceof org.bukkit.entity.Player player) {
                    if (!PlayerUtils.isSurvival(player) || isBlocking(player)) continue;
                    player.damage(10f);
                } else if (handle != this) {
                    living.damage(10f);
                }
            }
        }
        final Location location = this.getBukkitEntity().getLocation();
        location.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION, location, 1);
        this.remove(RemovalReason.KILLED);
    }

    private boolean isBlocking(org.bukkit.entity.Player player) {
        boolean blocking = player.isBlocking();
        if (!blocking) return false;
        Location playerLocation = player.getLocation();
        Vector blastDirection = this.getBukkitEntity().getLocation().subtract(playerLocation).toVector();
        Vector facing = playerLocation.getDirection();
        float angle = blastDirection.angle(facing);
        return angle < ANGLE_TO_BLOCK;
    }

    @Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 2d)
            .add(Attributes.FLYING_SPEED, 2d)
            .add(Attributes.FOLLOW_RANGE, 100)
            .build());
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof Player) {
            this.explode();
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public void registerGoals() {
        //navigation
        this.navigation = new FlyingPathNavigation(this, level());
        this.navigation.setCanFloat(true);

        this.moveControl = new FlyingMoveControl(this, 1, true); // no gravity true
        goalSelector.addGoal(1, new PathfinderGoalApproachSlowly(this, 1, 10, new AngeredSoulScream(this)));
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 1));
        targetSelector.addGoal(0, new PathfinderGoalClosestPlayer(this, SIGHT, true));
    }

    @Override
    public void reassessWeaponGoal() {
    }

}
