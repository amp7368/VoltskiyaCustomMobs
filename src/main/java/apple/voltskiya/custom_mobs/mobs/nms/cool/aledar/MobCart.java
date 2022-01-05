package apple.voltskiya.custom_mobs.mobs.nms.cool.aledar;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsMobWrapperQOLModel;
import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Objects;

public class MobCart extends EntityHorse implements NmsModelHolderQOL<MobCart> {
    private static NmsSpawnWrapperModel<MobCart> spawner;
    private NmsMobWrapperQOLModel<MobCart> wrapper;

    public MobCart(EntityTypes<MobCart> var0, World world) {
        super(DecodeEntityTypes.HORSE, world);
    }

    public static NmsSpawnWrapperModel<MobCart> spawner() {
        return spawner = Objects.requireNonNullElseGet(spawner, MobCart::makeSpawner);
    }

    public static NmsSpawnWrapperModel<MobCart> makeSpawner() {
        NmsModelHandler.ModelConfigName model = NmsModelHandler.ModelConfigName.PARASITE;
        return new NmsSpawnWrapperModel<>(
                model.getName(),
                MobCart::new,
                DecodeEntityTypes.ZOMBIE,
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
