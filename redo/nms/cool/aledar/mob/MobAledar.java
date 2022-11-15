package apple.voltskiya.custom_mobs.nms.cool.aledar.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.cool.aledar.config.MobAledarConfigImpl;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobRegisterConfigable;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobWrappedConfigable;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parts.child.MobPartArmorStand;
import apple.voltskiya.custom_mobs.nms.parts.child.MobPartChild;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobAledar extends EntityPillager implements RegisteredCustomMob, NmsMob<MobAledar, MobAledarConfigImpl> {
    public static final String IS_WHEEL_RIGHT_IDENTIFIER = "isRightWheel";
    public static final String IS_WHEEL_LEFT_IDENTIFIER = "isLeftWheel";

    public static final double SMALL_STAND_HEAD_RADIUS = .625 / 2 / 2;
    private static NmsMobRegisterConfigable<MobAledar, MobAledarConfigImpl> register;
    private final List<MobPartArmorStand> leftWheels = new ArrayList<>();
    private final List<MobPartArmorStand> rightWheels = new ArrayList<>();
    private NmsMobWrappedConfigable<MobAledar, MobAledarConfigImpl> mobManager;

    public MobAledar(EntityType<?> EntityType, World world) {
        super(DecodeEntityType.PILLAGER, world);
    }

    @Override
    public NmsMobWrappedConfigable<MobAledar, MobAledarConfigImpl> getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(NmsMobWrappedConfigable<MobAledar, MobAledarConfigImpl> mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public NmsMobRegisterConfigable<MobAledar, MobAledarConfigImpl> getRegister() {
        return register;
    }

    public static void setRegister(NmsMobRegisterConfigable<MobAledar, MobAledarConfigImpl> register) {
        MobAledar.register = register;
    }


    @Override
    public MobAledar getSelfEntity() {
        return this;
    }

    @Override
    public void preparePost() {
        for (MobPartChild part : mobManager.getChildren()) {
            if (part instanceof MobPartArmorStand armorStand) {
                Map<String, Object> otherData = part.getConfig().getData().otherData;
                if ((Boolean) otherData.getOrDefault(IS_WHEEL_RIGHT_IDENTIFIER, false)) {
                    rightWheels.add(armorStand);
                }
                if ((Boolean) otherData.getOrDefault(IS_WHEEL_LEFT_IDENTIFIER, false)) {
                    leftWheels.add(armorStand);
                }
            }
        }
        this.setInvisible(false);
    }

    @Override
    protected void u() {
        // mostly a villager
        GoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(0, new GoalFloat(this));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalAvoidTarget<>(this, EntityZoglin.class, 10.0F, 0.5D, 0.5D));
        goalSelector.a(1, new GoalPanic(this, 0.5D));
        goalSelector.a(4, new GoalMoveTowardsRestriction(this, 0.35D));
        goalSelector.a(8, new GoalRandomStrollLand(this, 0.35D));
        goalSelector.a(9, new GoalInteract(this, Player.class, 3.0F, 1.0F));
        goalSelector.a(10, new GoalLookAtPlayer(this, Mob.class, 8.0F));

        // mostly a cow
//        this.goalSelector.a(0, new GoalFloat(this));
//        this.goalSelector.a(1, new GoalPanic(this, 2.0D));
//        this.goalSelector.a(3, new GoalTempt(this, 1.25D, RecipeItemStack.a(new IMaterial[]{Items.WHEAT}), false));
//        this.goalSelector.a(5, new GoalRandomStrollLand(this, 1.0D));
//        this.goalSelector.a(6, new GoalLookAtPlayer(this, Player.class, 6.0F));
//        this.goalSelector.a(7, new GoalRandomLookaround(this));
    }


    @Override
    public void a(EnumMoveType enummovetype, Vec3 Vec3) {
        double moveAmount = Vec3.g();
//      move/(pi*2*r)*360; r = .625/2/2
        moveAmount *= 360;
        moveAmount /= Math.PI * 2 * SMALL_STAND_HEAD_RADIUS;
        if (moveAmount > 3) {
            // turn the wheels!
            for (MobPartArmorStand wheel : leftWheels) {
                wheel.rotateHead(0f, 0f, (float) -moveAmount);
            }
            for (MobPartArmorStand wheel : rightWheels) {
                wheel.rotateHead(0f, 0f, (float) moveAmount);
            }
        }
        nmsmove(enummovetype, Vec3);
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
    public EntityType<?> ad() {
        return nmsgetEntityType();
    }

    @Override
    public AttributeMap ep() {
        return nmsgetAttributeMap();
    }

    @Override
    public Entity b(ServerLevel ServerLevel) {
        return nmsChangeWorlds(ServerLevel);
    }

    @Override
    public void g(CompoundTag CompoundTag) {
        nmsload(CompoundTag);
    }

    @Override
    public CompoundTag f(CompoundTag CompoundTag) {
        return nmssave(CompoundTag);
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }
}
