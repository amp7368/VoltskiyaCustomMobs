package apple.voltskiya.custom_mobs.nms.nether.parasite;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOLModel;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler.ModelConfigName;
import apple.voltskiya.custom_mobs.pathfinders.GoalCraveBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.GoalFloat;
import net.minecraft.world.entity.ai.goal.GoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.GoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Objects;

public class MobParasite extends EntityZombie implements RegisteredCustomMob, NmsModelHolderQOL<MobParasite> {

    private static NmsSpawnWrapperModel<MobParasite> spawner;
    private NmsMobWrapperQOLModel<MobParasite> wrapper;

    public MobParasite(EntityType<MobParasite> EntityType, World world) {
        super(DecodeEntityType.ZOMBIE, world);
    }

    public static void spawn(Location location, CompoundTag oldNbt, Vector velocity) {
        MobParasite mob = spawner.spawn(location, oldNbt);
        CraftEntity bukkitEntity = mob.getBukkitEntity();
        if (velocity != null)
            bukkitEntity.teleport(bukkitEntity.getLocation().setDirection(velocity));
        bukkitEntity.setVelocity(velocity);
    }

    public static NmsSpawnWrapperModel<MobParasite> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobParasite::makeSpawner);
    }

    public static NmsSpawnWrapperModel<MobParasite> makeSpawner() {
        NmsModelHandler.ModelConfigName model = ModelConfigName.PARASITE;
        return new NmsSpawnWrapperModel<>(
            model.getName(),
            MobParasite::new,
            DecodeEntityType.ZOMBIE,
            model
        );
    }

    // init pathfinder
    @Override
    protected void u() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            // if this is the overworld, do the overworld AI, otherwise, get to the overworld
            if (this.getBukkitEntity().getWorld().isNatural()) {
                this.initPathfinderOverworld();
            } else {
                this.initPathfinderOtherWorld();
            }
        }, 5 * 10);
    }

    private void initPathfinderOtherWorld() {
        // new GoalRandomStrollLand(entityToMove, speed, chanceToActivate)
        GoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(0, new GoalFloat(this));
        goalSelector.a(1, new GoalCraveBlock(this, Collections.singletonList(org.bukkit.Material.NETHER_PORTAL), 512, 20, 2, 1.0));
        goalSelector.a(2, new GoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    private void initPathfinderOverworld() {
        final int chanceToCheck = 20;
        int chanceToCheckPlayer = 60;
        GoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, new NearestAttackableTargetGoal<>(this, EntityAnimal.class, chanceToCheck, true, false,
            (entityLiving) -> !entityLiving.getBukkitEntity().getScoreboardTags().contains(MobInfected.PARASITE_INFECTED_TAG)));
        targetSelector.a(2, new NearestAttackableTargetGoal<>(this, EntityPlayer.class, chanceToCheckPlayer, true, false, null));
        double speed = 1.2;
        GoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(1, new GoalMeleeAttack(this, speed, true));
        goalSelector.a(2, new GoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    // attack entity
    @Override
    public boolean z(Entity e) {
        if (!(e instanceof PathfinderMob entity) || e.getBukkitEntity().getScoreboardTags()
            .contains(MobInfected.PARASITE_INFECTED_TAG)) {
            return super.z(e);
        }
        new MobInfected(entity);
        die();
        return false;
    }

    @Override
    public EnumMonsterType eq() {
        return DecodeEnumMonsterType.ARTHROPOD.encode();
    }

    @Override
    public MobParasite getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapperModel<MobParasite> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOLModel<MobParasite> getSelfWrapper() {
        return wrapper = Objects.requireNonNullElseGet(wrapper, NmsModelHolderQOL.super::makeSelfWrapper);
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
