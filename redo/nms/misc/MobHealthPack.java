package apple.voltskiya.custom_mobs.nms.misc;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeDamageSource;
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
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class MobHealthPack extends EntityZombie implements RegisteredCustomMob, NmsHolderQOL<MobHealthPack> {
    public static final String REGISTERED_NAME = "health_pack";

    private static NmsSpawnWrapper<MobHealthPack> spawner;
    private NmsMobWrapperQOL<MobHealthPack> wrapper;


    public MobHealthPack(EntityType<MobHealthPack> EntityType, World world) {
        super(DecodeEntityType.ZOMBIE, world);
    }

    public static NmsSpawnWrapper<MobHealthPack> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobHealthPack::makeSpawner);
    }

    public static NmsSpawnWrapper<MobHealthPack> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobHealthPack::new,
                DecodeEntityType.ZOMBIE
        );
    }

    // collide
    @Override
    public void g(Entity entity) {
        if (entity instanceof Player human) {
            this.healPlayer(human);
            DecodeEntity.die(this, DecodeDamageSource.OUT_OF_WORLD);
        }
    }

    private void healPlayer(Player player) {
        double health = ((LivingEntity) getBukkitEntity()).getHealth();
        CraftHumanEntity playerBukkit = player.getBukkitEntity();
        health = playerBukkit.getHealth() + health;
        health = Math.min(playerBukkit.getMaxHealth(), health);
        playerBukkit.setHealth(health);
    }

    // initPathfinder
    @Override
    protected void u() {
    }

    @Override
    public MobHealthPack getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapper<MobHealthPack> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOL<MobHealthPack> getSelfWrapper() {
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
