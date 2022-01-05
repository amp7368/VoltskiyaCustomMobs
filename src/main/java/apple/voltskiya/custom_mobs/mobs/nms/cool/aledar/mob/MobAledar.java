package apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.config.MobAledarConfigImpl;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegisterConfigable;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobWrappedConfigable;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

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

    public MobAledar(EntityTypes<?> entitytypes, World world) {
        super(DecodeEntityTypes.PILLAGER, world);
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
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZoglin.class, 10.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalPanic(this, 0.5D));
        goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 0.35D));
        goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 0.35D));
        goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));

        // mostly a cow
//        this.goalSelector.a(0, new PathfinderGoalFloat(this));
//        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
//        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a(new IMaterial[]{Items.WHEAT}), false));
//        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
//        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
//        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }


    @Override
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        double moveAmount = vec3d.g();
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
        nmsmove(enummovetype, vec3d);
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
    public EntityTypes<?> ad() {
        return nmsgetEntityType();
    }

    @Override
    public AttributeMapBase ep() {
        return nmsgetAttributeMap();
    }

    @Override
    public Entity b(WorldServer worldserver) {
        return nmsChangeWorlds(worldserver);
    }

    @Override
    public void g(NBTTagCompound nbttagcompound) {
        nmsload(nbttagcompound);
    }

    @Override
    public NBTTagCompound f(NBTTagCompound nbttagcompound) {
        return nmssave(nbttagcompound);
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }
}
