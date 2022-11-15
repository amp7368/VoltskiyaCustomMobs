package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant.trash;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.pathfinders.GoalBowShootNoBow;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class MobRevenantOld extends EntitySkeleton implements RegisteredCustomMob, NmsHolderQOL<MobRevenantOld> {
    public static final String REGISTERED_NAME = "revenant";
    private static NmsSpawnWrapper<MobRevenantOld> spawner;
    private NmsMobWrapperQOL<MobRevenantOld> wrapper;

    public MobRevenantOld(EntityType<? extends EntitySkeleton> EntityType, World world) {
        super(DecodeEntityType.SKELETON, world);
    }

    @Override
    public AttributeSupplier getAttributeSupplier() {
        return EntitySkeletonAbstract.n()
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 50)
                .a();
    }

    public static NmsSpawnWrapper<MobRevenantOld> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobRevenantOld::makeSpawner);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.a(packetplayoutspawnentityliving);
    }

    public static NmsSpawnWrapper<MobRevenantOld> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobRevenantOld::new,
                DecodeEntityType.ZOMBIE
        );
    }

    // initpathfinder
    @Override
    protected void u() {
        super.u();
        DecodeEntity.getGoalSelector(this).a(4, new GoalBowShootNoBow<>(this, 1.0D, 20, 15.0F));
    }

    @Override
    public void t() {
        // do no *special* pathfinding
    }

    @Override
    public MobRevenantOld getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobRevenantOld> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobRevenantOld> getSelfWrapper() {
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
    public EntityType<?> ad() {
        return nmsgetEntityType();
    }

    @Override
    public void a(EnumMoveType enummovetype, Vec3 Vec3) {
        nmsmove(enummovetype, Vec3);
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

    public void setWrapper(NmsMobWrapperQOL<MobRevenantOld> wrapper) {
        this.wrapper = wrapper;
    }
}
