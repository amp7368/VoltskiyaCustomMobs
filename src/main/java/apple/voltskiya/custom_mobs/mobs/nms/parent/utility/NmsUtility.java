package apple.voltskiya.custom_mobs.mobs.nms.parent.utility;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.nbt.DecodeNBT;
import apple.utilities.util.ObjectUtilsFormatting;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface NmsUtility<SelfEntity extends Entity> extends DecodeEntity {
    default void prepare(Location location, NBTTagCompound oldNbt) {
        NmsModel selfModel = getSelfModel();
        final NBTTagCompound newNbt = ObjectUtilsFormatting.failToNull(selfModel, NmsModel::mainPart, (m) -> m.getData().nbt);
        final NBTTagCompound mergedNbt;
        if (oldNbt == null) {
            mergedNbt = Objects.requireNonNullElseGet(newNbt, NBTTagCompound::new);
        } else mergedNbt = DecodeNBT.merge(oldNbt, newNbt);
        DecodeNBT.removeKey(mergedNbt, "UUID");
        SelfEntity selfEntity = getSelfEntity();
        DecodeEntity.load(selfEntity, mergedNbt);
        selfEntity.getBukkitEntity().teleport(location);
        preparePostHook();
        preparePost();
    }

    default Entity getEntity() {
        return getSelfEntity();
    }

    default void preparePostHook() {
    }

    default void preparePost() {
    }

    ///////////////////////////////////

    NmsMobEntitySupers getEntitySupers();

    NmsMobEntitySupers makeEntitySupers();

    ///////////////////////////////////

    default void nmsmove(EnumMoveType enummovetype, Vec3D vec3d) {
        getEntitySupers().move(enummovetype, vec3d);
        movePostHook();
        movePost();
    }

    default void movePostHook() {
    }

    default void movePost() {
    }


    @Nullable
    default Entity nmsChangeWorlds(WorldServer worldserver) {
        final Entity result = getEntitySupers().changeWorlds(worldserver);
        changeWorldsPostHook(result);
        changeWorldsPost(result);
        return result;
    }

    default void changeWorldsPost(Entity result) {
    }

    default void changeWorldsPostHook(Entity result) {
    }


    default void nmsload(NBTTagCompound nbttagcompound) {
        DecodeNBT.setString(nbttagcompound, "id", getSaveId());
        getEntitySupers().load(nbttagcompound);
        loadPostHook();
        loadPost();
    }

    default void loadPost() {
    }

    default void loadPostHook() {
    }

    default NBTTagCompound nmssave(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbt = getEntitySupers().save(nbttagcompound);
        DecodeNBT.setString(nbt, "id", getSaveId());
        savePostHook(nbt);
        savePost(nbt);
        return nbt;
    }

    default void savePost(NBTTagCompound nbt) {
    }

    default void savePostHook(NBTTagCompound nbt) {
    }


    default void nmsRemove(Entity.RemovalReason removalReason) {
        getEntitySupers().remove(removalReason);
        removePostHook();
        removePost();
        this.removePostHook();
    }

    default void removePost() {
    }

    default void removePostHook() {
    }

    ///////////////////////////////////


    String getSaveId();

    SelfEntity getSelfEntity();

    EntityTypes<?> nmsgetEntityType();

    AttributeMapBase nmsgetAttributeMap();

    @Nullable
    default NmsModel getSelfModel() {
        return null;
    }

    default boolean hasModel() {
        return getSelfModel() != null;
    }

    default AttributeProvider getAttributeProvider() {
        @SuppressWarnings("unchecked") EntityTypes<? extends EntityLiving> entityType = (EntityTypes<? extends EntityLiving>) getEntityType();
        return AttributeDefaults.a(entityType);
    }

    // getEntityType
    //////////////////////
    default EntityTypes<?> ad() {
        return nmsgetEntityType();
    }

    // getAttributeMap
    default AttributeMapBase ep() {
        return nmsgetAttributeMap();
    }

    // move
    default void a(EnumMoveType enummovetype, Vec3D vec3d) {
        nmsmove(enummovetype, vec3d);
    }

    // change worlds
    default Entity b(WorldServer worldserver) {
        return nmsChangeWorlds(worldserver);
    }

    // load
    default void g(NBTTagCompound nbttagcompound) {
        nmsload(nbttagcompound);
    }

    // save
    default NBTTagCompound f(NBTTagCompound nbttagcompound) {
        return nmssave(nbttagcompound);
    }

    default void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }

    // UTILITY METHODS
    //////////////////////
    default EntityTypes<?> getEntityType() {
        return this.ad();
    }

    default AttributeMapBase getAttributeMap() {
        return ep();
    }

    default Entity changeWorlds(WorldServer world) {
        return b(world);
    }

    default void load(NBTTagCompound nbt) {
        this.g(nbt);
    }

    default NBTTagCompound save(NBTTagCompound nbt) {
        return f(nbt);
    }

    default NBTTagCompound save() {
        return save(new NBTTagCompound());
    }

    default void remove(Entity.RemovalReason removalReason) {
        a(removalReason);
    }

    default void teleport(Location newLocation) {
        getBukkitEntity().teleport(newLocation);
    }

    default Location getLocation() {
        return getBukkitEntity().getLocation();
    }

    default Supplier<GameProfilerFiller> getMethodProfilerSupplier() {
        return () -> DecodeEntity.getMethodProfiler(getSelfEntity());
    }

    default void setInvisible(boolean invis) {
        getBukkitLivingEntity().setInvisible(invis);
    }

    // GET BUKKIT METHODS
    //////////////////////
    default org.bukkit.entity.Entity getBukkitEntity() {
        return getSelfEntity().getBukkitEntity();
    }

    default LivingEntity getBukkitLivingEntity() {
        return (LivingEntity) getBukkitEntity();
    }

    default <T extends org.bukkit.entity.Entity> T getBukkitEntityTyped(Class<T> typed) {
        return typed.cast(getBukkitEntity());
    }

    default EntityInsentient getEntityInsentient() {
        return (EntityInsentient) getSelfEntity();
    }
}
