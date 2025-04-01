package apple.voltskiya.custom_mobs.nms.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.World;

public interface INmsMob<Self extends Entity> {

    Self getSelf();

    NmsMob<Self> wrapper();

    default NmsMob<Self> createWrapper() {
        return new NmsMob<>(getSelf(), getInstSpawner(), makeEntitySupers());
    }

    NmsSpawner<Self, ?> getInstSpawner();

    NmsMobSupers<Self> makeEntitySupers();

    default void prepare() {
    }

    default Entity nmsChangeWorlds(ServerLevel server) {
        throw new NotImplementedException();
    }

    default void nmsMove(MoverType moverType, Vec3 Vec3) {
        supers().move().accept(moverType, Vec3);
    }

    default void nmsLoad(CompoundTag CompoundTag) {
        supers().load().accept(CompoundTag);
    }

    default boolean nmsSave(CompoundTag nbt) {
        return supers().save().apply(nbt);
    }

    default CompoundTag nmsSaveWithoutId(CompoundTag nbt) {
        return supers().saveWithoutId().apply(nbt);
    }

    default void remove(RemovalReason removalReason) {
        supers().remove().accept(removalReason);
    }

    default World getBukkitWorld() {
        return getSelf().getBukkitEntity().getWorld();
    }

    private NmsMobSupers<Self> supers() {
        return wrapper().supers();
    }

}
