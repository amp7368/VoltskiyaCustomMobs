package apple.voltskiya.custom_mobs.nms.nether.angered_soul;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.nms.decoding.pathfinder.DecodeControllerMoveFlying;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.pathfinders.GoalApproachSlowly;
import apple.voltskiya.custom_mobs.pathfinders.target_selector.GoalClosestPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.GoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.constants.TagConstants;

import java.util.List;
import java.util.Objects;

public class MobAngeredSoul extends EntitySkeleton implements RegisteredCustomMob, NmsHolderQOL<MobAngeredSoul> {
    public static final String REGISTERED_NAME = "angered_soul";
    private static final double SIGHT = 100;
    public static final double EXPLOSION_RADIUS = 1.5;
    private static NmsSpawnWrapper<MobAngeredSoul> spawner;
    private NmsMobWrapperQOL<MobAngeredSoul> wrapper;

    public MobAngeredSoul(EntityType<MobAngeredSoul> EntityType, World world) {
        super(DecodeEntityType.SKELETON, world);
    }

    public static NmsSpawnWrapper<MobAngeredSoul> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, () -> new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobAngeredSoul::new,
                DecodeEntityType.SKELETON
        ));
    }

    @Override
    public CompoundTag f(CompoundTag CompoundTag) {
        return nmssave(CompoundTag);
    }

    @Override
    public NmsMobEntitySupers makeEntitySupers() {
        return new NmsMobEntitySupers(
                super::b, // change world
                super::a, // move
                super::g, //load
                super::f, //save
                super::a // die
        );
    }

    @Override
    public void preparePost() {
        getBukkitEntity().addScoreboardTag(TagConstants.NO_FALL_DAMAGE);
    }

    @Override
    public MobAngeredSoul getSelfEntity() {
        return this;
    }

    // collide
    @Override
    public void g(Entity entity) {
        super.g(entity);
        if (entity instanceof Player) {
            this.explode();
        }
    }

    public void explode() {
        List<org.bukkit.entity.Entity> nearbyEntities = this.getBukkitEntity().getNearbyEntities(EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS);
        for (org.bukkit.entity.Entity nearby : nearbyEntities) {
            if (nearby instanceof LivingEntity living) {
                final EntityLiving handle = ((CraftLivingEntity) nearby).getHandle();
                if (handle == this) {
                    return;
                }
                if (nearby instanceof Player player) {
                    if (player.getGameMode() == GameMode.SURVIVAL && !player.isBlocking()) {
                        player.damage(10f);
                    }
                } else {
                    living.damage(10f);
                }
            }
        }
        final Location location = this.getBukkitEntity().getLocation();
        location.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_LARGE, location, 1);
        this.removePostHook();
    }

    public AttributeSupplier getAttributeSupplier() {
        return EntityMonster.fD()
                .a(DecodeGenericAttributes.MOVEMENT_SPEED, 0.25D)
                .a(DecodeGenericAttributes.FLYING_SPEED, .5d)
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 100)
                .a();
    }

    @Override
    public EntityType<?> ad() {
        return spawner().EntityType();
    }

    // isOnGround
    @Override
    public boolean aw() {
        return false;
    }

    @Override
    protected void u() {
        //navigation
        this.bQ = new NavigationFlying(this, DecodeEntity.getWorld(this));
        this.bO = new ControllerMoveGhost(this, 1d); // no gravity true
        GoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        GoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        goalSelector.a(1, new GoalApproachSlowly(this, 1, 10, new AngeredSoulScream(this)));
        goalSelector.a(0, new GoalLookAtPlayer(this, Player.class, 1));
        targetSelector.a(1, new GoalClosestPlayer(this, SIGHT, true));
    }

    @Override
    public void t() {
        // do no special pathfinding
    }

    @Override
    public NmsSpawnWrapper<MobAngeredSoul> getSpawner() {
        return spawner;
    }

    @Override
    public NmsMobWrapperQOL<MobAngeredSoul> getSelfWrapper() {
        return wrapper = Objects.requireNonNullElseGet(wrapper, NmsHolderQOL.super::makeSelfWrapper);
    }

    private static class ControllerMoveGhost extends DecodeControllerMoveFlying {
        public ControllerMoveGhost(Mob me, double speed) {
            // me, speed, noGravity
            super(me, (int) speed, true);
            me.getBukkitEntity().setGravity(false);
        }

        public void a() {
            super.a();
            // check we're moving instead of jumping or whatever
            if (this.k == DecodeOperation.MOVE_TO.encode()) {
                // set the y change to be a smooth ascent or descent
                double var2 = y() - d.getBukkitEntity().getLocation().getY();
                // change y value to what it should be rather than oscillating with the target
                DecodeEntity.setYMove(me(), (float) var2);
            }
        }
    }
}
