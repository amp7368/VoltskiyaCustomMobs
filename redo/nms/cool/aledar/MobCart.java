package apple.voltskiya.custom_mobs.nms.cool.aledar;

import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsMobWrapperQOLModel;
import apple.voltskiya.custom_mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import voltskiya.apple.utilities.minecraft.InventoryUtils;

import java.util.Objects;

public class MobCart extends EntityHorse implements NmsModelHolderQOL<MobCart> {
    private static NmsSpawnWrapperModel<MobCart> spawner;
    private NmsMobWrapperQOLModel<MobCart> wrapper;

    public MobCart(EntityType<MobCart> var0, World world) {
        super(DecodeEntityType.HORSE, world);
    }

    public static NmsSpawnWrapperModel<MobCart> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobCart::makeSpawner);
    }

    public static NmsSpawnWrapperModel<MobCart> makeSpawner() {
        NmsModelHandler.ModelConfigName model = NmsModelHandler.ModelConfigName.PARASITE;
        return new NmsSpawnWrapperModel<>(
                model.getName(),
                MobCart::new,
                DecodeEntityType.ZOMBIE,
                model
        );
    }

    @Override
    public void preparePost() {
        this.setInvisible(true);
        Horse bukkitEntity = this.getBukkitEntityTyped(Horse.class);
        bukkitEntity.setTamed(true);
        bukkitEntity.setBaby();
        bukkitEntity.setAgeLock(true);
        bukkitEntity.getInventory().setSaddle(InventoryUtils.makeItem(Material.SADDLE));
    }

    // isBaby
    @Override
    public boolean y_() {
        return false;
    }

    @Override
    protected void u() {
        // do no pathfinding
    }

    @Override
    public MobCart getSelfEntity() {
        return this;
    }

    @Override
    public NmsSpawnWrapperModel<MobCart> getSpawner() {
        return spawner();
    }

    @Override
    public NmsMobWrapperQOLModel<MobCart> getSelfWrapper() {
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
