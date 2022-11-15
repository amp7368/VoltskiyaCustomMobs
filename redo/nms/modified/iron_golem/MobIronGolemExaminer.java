package apple.voltskiya.custom_mobs.mobs.modified.iron_golem;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.pathfinders.utilities.HurtByTargetGoalExcept;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.GoalDefendVillage;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.GoalUniversalAngerReset;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class MobIronGolemExaminer extends EntityIronGolem implements RegisteredCustomMob, NmsHolderQOL<MobIronGolemExaminer> {
    public static final String REGISTERED_NAME = "mob.examiner.iron_golem";
    private static NmsSpawnWrapper<MobIronGolemExaminer> spawner;
    private NmsMobWrapperQOL<MobIronGolemExaminer> wrapper;

    public MobIronGolemExaminer(EntityType<? extends EntityIronGolem> EntityType, World world) {
        super(DecodeEntityType.IRON_GOLEM, world);
    }

    public static NmsSpawnWrapper<MobIronGolemExaminer> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobIronGolemExaminer::makeSpawner);
    }

    public static NmsSpawnWrapper<MobIronGolemExaminer> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobIronGolemExaminer::new,
                DecodeEntityType.IRON_GOLEM
        );
    }

    @Override
    protected void u() {
        // copied from super.initPathfinder()
        super.u();
        DecodeEntity.setTargetSelector(this, new GoalSelector(getMethodProfilerSupplier()));
        GoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, new GoalDefendVillage(this));
        // modified to not attack illagers
        targetSelector.a(2, new HurtByTargetGoalExcept(this, (e) -> DecodeEntity.getMonsterType(e) != DecodeEnumMonsterType.ILLAGER.encode()));
        targetSelector.a(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::a_));
        // modified to not attack illagers
        targetSelector.a(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (entityliving) -> entityliving instanceof IMonster &&
                        !(entityliving instanceof EntityCreeper) &&
                        DecodeEntity.getMonsterType(entityliving) != DecodeEnumMonsterType.ILLAGER.encode())
        );
        targetSelector.a(4, new GoalUniversalAngerReset<>(this, false));
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
}
