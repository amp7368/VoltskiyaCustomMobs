package apple.voltskiya.custom_mobs.mobs.modified.illager.pillager;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class MobIllagerPillagerExaminer extends EntityPillager implements RegisteredCustomMob, NmsHolderQOL<MobIllagerPillagerExaminer> {
    public static final String REGISTERED_NAME = "mob.examiner.pillager";
    private static NmsSpawnWrapper<MobIllagerPillagerExaminer> spawner;
    private NmsMobWrapperQOL<MobIllagerPillagerExaminer> wrapper;

    public MobIllagerPillagerExaminer(EntityType<?> EntityType, World world) {
        super(DecodeEntityType.PILLAGER, world);
    }

    public static NmsSpawnWrapper<MobIllagerPillagerExaminer> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobIllagerPillagerExaminer::makeSpawner);
    }

    public static NmsSpawnWrapper<MobIllagerPillagerExaminer> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobIllagerPillagerExaminer::new,
                DecodeEntityType.PILLAGER
        );
    }

    @Override
    protected void u() {
        super.u();
        DecodeEntity.setTargetSelector(this, new GoalSelector(getMethodProfilerSupplier()));
        GoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, (new HurtByTargetGoal(this, EntityIronGolem.class, EntityRaider.class)).a(new Class[0]));
        targetSelector.a(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).c(300));
        targetSelector.a(3, (new NearestAttackableTargetGoal<>(this, EntityVillagerAbstract.class, false)).c(300));
    }

    @Override
    public MobIllagerPillagerExaminer getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobIllagerPillagerExaminer> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobIllagerPillagerExaminer> getSelfWrapper() {
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
