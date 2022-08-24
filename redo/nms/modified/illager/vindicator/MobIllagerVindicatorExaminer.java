package apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.EntityVindicator;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

import java.util.Objects;

public class MobIllagerVindicatorExaminer extends EntityVindicator implements RegisteredCustomMob, NmsHolderQOL<MobIllagerVindicatorExaminer> {
    private static final String REGISTERED_NAME = "mob.examiner.vindicator";

    private static NmsSpawnWrapper<MobIllagerVindicatorExaminer> spawner;
    private NmsMobWrapperQOL<MobIllagerVindicatorExaminer> wrapper;

    public MobIllagerVindicatorExaminer(EntityTypes<? extends EntityVindicator> entitytypes, World world) {
        super(DecodeEntityTypes.VINDICATOR, world);
    }

    public static NmsSpawnWrapper<MobIllagerVindicatorExaminer> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobIllagerVindicatorExaminer::makeSpawner);
    }

    public static NmsSpawnWrapper<MobIllagerVindicatorExaminer> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobIllagerVindicatorExaminer::new,
                DecodeEntityTypes.VINDICATOR
        );
    }

    @Override
    protected void u() {
        super.u();
        DecodeEntity.setTargetSelector(this, new GoalSelector(getMethodProfilerSupplier()));
        GoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, (new HurtByTargetGoal(this, EntityVindicator.class, EntityRaider.class)).a(new Class[0]));
        targetSelector.a(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).c(300));
        targetSelector.a(3, (new NearestAttackableTargetGoal<>(this, EntityVillagerAbstract.class, false)).c(300));
    }

    @Override
    public MobIllagerVindicatorExaminer getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobIllagerVindicatorExaminer> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobIllagerVindicatorExaminer> getSelfWrapper() {
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
