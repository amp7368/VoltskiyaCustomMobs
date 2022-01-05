package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33ConfigWhole;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegisterConfigable;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobWrappedConfigable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

public class MobAPC33Whole extends EntityRavager implements NmsMob<MobAPC33Whole, MobAPC33ConfigWhole> {
    private static NmsMobRegisterConfigable<MobAPC33Whole, MobAPC33ConfigWhole> register;
    private NmsMobWrappedConfigable<MobAPC33Whole, MobAPC33ConfigWhole> mobManager = null;
    private MobAPC33Treads treadsMob;
    private MobAPC33SmallGun sideGunMob;
    private MobAPC33LargeCannon cannonMob;


    public MobAPC33Whole(EntityTypes<MobAPC33Whole> entityTypes, World world) {
        super(DecodeEntityTypes.RAVAGER, world);
    }

    @Override
    public NmsMobWrappedConfigable<MobAPC33Whole, MobAPC33ConfigWhole> getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(NmsMobWrappedConfigable<MobAPC33Whole, MobAPC33ConfigWhole> mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public NmsMobRegisterConfigable<MobAPC33Whole, MobAPC33ConfigWhole> getRegister() {
        return register;
    }

    public static void setRegister(NmsMobRegisterConfigable<MobAPC33Whole, MobAPC33ConfigWhole> register) {
        MobAPC33Whole.register = register;
    }

    @Override
    public MobAPC33Whole getSelfEntity() {
        return this;
    }

    @Override
    public void preparePost() {
        Location location = getLocation();
        NBTTagCompound oldNbt = save();
        this.cannonMob = MobAPC33LargeCannon.getRegisterStatic().spawn(location, oldNbt);
        this.sideGunMob = MobAPC33SmallGun.getRegisterStatic().spawn(location, oldNbt);
        this.treadsMob = MobAPC33Treads.getRegisterStatic().spawn(location, oldNbt);

        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
            DecodeEntity.startRiding(cannonMob, sideGunMob);
            DecodeEntity.startRiding(sideGunMob, this);
            DecodeEntity.startRiding(this, treadsMob);
        }, 1);
    }

    @Override
    protected void u() {
        super.u();
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
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
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        float oldHeadRotation = getBukkitEntity().getLocation().getYaw();
        getEntitySupers().move(enummovetype, vec3d);
        // slow the head rotation
        Location newLocation = getBukkitEntity().getLocation();
        float newHeadRotation = newLocation.getYaw();
        double differenceHeadRotation = newHeadRotation - oldHeadRotation;
        if (differenceHeadRotation < 0) {
            differenceHeadRotation = Math.max(differenceHeadRotation, getConfig().maxHeadRotationPerTick);
        } else {
            differenceHeadRotation = Math.min(differenceHeadRotation, getConfig().maxHeadRotationPerTick);
        }
        newLocation.setYaw((float) (oldHeadRotation + differenceHeadRotation));
        teleport(newLocation);
        this.movePostHook();
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

