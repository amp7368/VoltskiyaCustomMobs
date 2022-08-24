package apple.voltskiya.custom_mobs.mobs.nms.misc;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.iregistry.DecodeDamageSource;
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
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class MobHealthPack extends EntityZombie implements RegisteredCustomMob, NmsHolderQOL<MobHealthPack> {
    public static final String REGISTERED_NAME = "health_pack";

    private static NmsSpawnWrapper<MobHealthPack> spawner;
    private NmsMobWrapperQOL<MobHealthPack> wrapper;


    public MobHealthPack(EntityTypes<MobHealthPack> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    public static NmsSpawnWrapper<MobHealthPack> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobHealthPack::makeSpawner);
    }

    public static NmsSpawnWrapper<MobHealthPack> makeSpawner() {
        return new NmsSpawnWrapper<>(
                REGISTERED_NAME,
                MobHealthPack::new,
                DecodeEntityTypes.ZOMBIE
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
