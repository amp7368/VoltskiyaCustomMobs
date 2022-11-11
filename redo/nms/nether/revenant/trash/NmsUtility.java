package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant.trash;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.nbt.DecodeNBT;
import apple.utilities.util.ObjectUtilsFormatting;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parts.NmsModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface NmsUtility<SelfEntity extends Entity> extends DecodeEntity {
    default void prepare(Location location, CompoundTag oldNbt) {
        NmsModel selfModel = getSelfModel();
        final CompoundTag newNbt = ObjectUtilsFormatting.failToNull(selfModel, NmsModel::mainPart, (m) -> m.getData().nbt);
        final CompoundTag mergedNbt;
        if (oldNbt == null) {
            mergedNbt = Objects.requireNonNullElseGet(newNbt, CompoundTag::new);
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

    default void nmsmove(EnumMoveType enummovetype, Vec3 Vec3) {
        getEntitySupers().move(enummovetype, Vec3);
        movePostHook();
        movePost();
    }

    default void movePostHook() {
    }

    default void movePost() {
    }


    @Nullable
    default Entity nmsChangeWorlds(ServerLevel ServerLevel) {
        final Entity result = getEntitySupers().changeWorlds(ServerLevel);
        changeWorldsPostHook(result);
        changeWorldsPost(result);
        return result;
    }

    default void changeWorldsPost(Entity result) {
    }

    default void changeWorldsPostHook(Entity result) {
    }


    default void nmsload(CompoundTag CompoundTag) {
        DecodeNBT.setString(CompoundTag, "id", getSaveId());
        getEntitySupers().load(CompoundTag);
        loadPostHook();
        loadPost();
    }

    default void loadPost() {
    }

    default void loadPostHook() {
    }

    default CompoundTag nmssave(CompoundTag CompoundTag) {
        CompoundTag nbt = getEntitySupers().save(CompoundTag);
        DecodeNBT.setString(nbt, "id", getSaveId());
        savePostHook(nbt);
        savePost(nbt);
        return nbt;
    }

    default void savePost(CompoundTag nbt) {
    }

    default void savePostHook(CompoundTag nbt) {
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

    EntityType<?> nmsgetEntityType();

    AttributeMap nmsgetAttributeMap();

    @Nullable
    default NmsModel getSelfModel() {
        return null;
    }

    default boolean hasModel() {
        return getSelfModel() != null;
    }

    default AttributeSupplier getAttributeSupplier() {
        @SuppressWarnings("unchecked") EntityType<? extends EntityLiving> entityType = (EntityType<? extends EntityLiving>) getEntityType();
        return DefaultAttributes.a(entityType);
    }

    // getEntityType
    //////////////////////
    default EntityType<?> ad() {
        return nmsgetEntityType();
    }

    // getAttributeMap
    default AttributeMap ep() {
        return nmsgetAttributeMap();
    }

    // move
    default void a(EnumMoveType enummovetype, Vec3 Vec3) {
        nmsmove(enummovetype, Vec3);
    }

    // change worlds
    default Entity b(ServerLevel ServerLevel) {
        return nmsChangeWorlds(ServerLevel);
    }

    // load
    default void g(CompoundTag CompoundTag) {
        nmsload(CompoundTag);
    }

    // save
    default CompoundTag f(CompoundTag CompoundTag) {
        return nmssave(CompoundTag);
    }

    default void a(Entity.RemovalReason removalReason) {
        nmsRemove(removalReason);
    }

    // UTILITY METHODS
    //////////////////////
    default EntityType<?> getEntityType() {
        return this.ad();
    }

    default AttributeMap getAttributeMap() {
        return ep();
    }

    default Entity changeWorlds(ServerLevel world) {
        return b(world);
    }

    default void load(CompoundTag nbt) {
        this.g(nbt);
    }

    default CompoundTag save(CompoundTag nbt) {
        return f(nbt);
    }

    default CompoundTag save() {
        return save(new CompoundTag());
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

    default Mob getMob() {
        return (Mob) getSelfEntity();
    }
}
