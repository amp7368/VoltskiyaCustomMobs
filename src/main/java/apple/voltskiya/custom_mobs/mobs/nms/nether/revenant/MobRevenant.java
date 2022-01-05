package apple.voltskiya.custom_mobs.mobs.nms.nether.revenant;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

import java.util.Objects;

public class MobRevenant extends EntitySkeleton implements RegisteredCustomMob, NmsHolderQOL<MobRevenant> {
    public static final String REGISTERED_NAME = "revenant";
    private static NmsSpawnWrapper<MobRevenant> spawner;
    private NmsMobWrapperQOL<MobRevenant> wrapper;

    public MobRevenant(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(DecodeEntityTypes.SKELETON, world);
    }

    @Override
    public AttributeProvider getAttributeProvider() {
        return EntitySkeletonAbstract.n()
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 50)
                .a();
    }

    public static NmsSpawnWrapper<MobRevenant> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobRevenant::makeSpawner);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.a(packetplayoutspawnentityliving);
    }

    public static NmsSpawnWrapper<MobRevenant> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobRevenant::new,
                DecodeEntityTypes.ZOMBIE
        );
    }

    // initpathfinder
    @Override
    protected void u() {
        super.u();
        DecodeEntity.getGoalSelector(this).a(4, new PathfinderGoalBowShootNoBow<>(this, 1.0D, 20, 15.0F));
    }

    @Override
    public void t() {
        // do no *special* pathfinding
    }

    @Override
    public MobRevenant getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobRevenant> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobRevenant> getSelfWrapper() {
        return wrapper = Objects.requireNonNullElseGet(wrapper, NmsHolderQOL.super::makeSelfWrapper);
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
    public void a(EnumMoveType enummovetype, Vec3D vec3d) {
        nmsmove(enummovetype, vec3d);
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

    public void setWrapper(NmsMobWrapperQOL<MobRevenant> wrapper) {
        this.wrapper = wrapper;
    }
}
