package apple.voltskiya.custom_mobs.mobs.modified.iron_golem;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalHurtByTargetExcept;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalDefendVillage;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

import java.util.Objects;

public class MobIronGolemExaminer extends EntityIronGolem implements RegisteredCustomMob, NmsHolderQOL<MobIronGolemExaminer> {
    public static final String REGISTERED_NAME = "mob.examiner.iron_golem";
    private static NmsSpawnWrapper<MobIronGolemExaminer> spawner;
    private NmsMobWrapperQOL<MobIronGolemExaminer> wrapper;

    public MobIronGolemExaminer(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(DecodeEntityTypes.IRON_GOLEM, world);
    }

    public static NmsSpawnWrapper<MobIronGolemExaminer> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobIronGolemExaminer::makeSpawner);
    }

    public static NmsSpawnWrapper<MobIronGolemExaminer> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobIronGolemExaminer::new,
                DecodeEntityTypes.IRON_GOLEM
        );
    }

    @Override
    protected void u() {
        // copied from super.initPathfinder()
        super.u();
        DecodeEntity.setTargetSelector(this, new PathfinderGoalSelector(getMethodProfilerSupplier()));
        PathfinderGoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        // modified to not attack illagers
        targetSelector.a(2, new PathfinderGoalHurtByTargetExcept(this, (e) -> DecodeEntity.getMonsterType(e) != DecodeEnumMonsterType.ILLAGER.encode()));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        // modified to not attack illagers
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 5, false, false,
                (entityliving) -> entityliving instanceof IMonster &&
                        !(entityliving instanceof EntityCreeper) &&
                        DecodeEntity.getMonsterType(entityliving) != DecodeEnumMonsterType.ILLAGER.encode())
        );
        targetSelector.a(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    @Override
    public MobIronGolemExaminer getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobIronGolemExaminer> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobIronGolemExaminer> getSelfWrapper() {
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
}
