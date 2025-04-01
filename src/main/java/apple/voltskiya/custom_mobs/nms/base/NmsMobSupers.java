package apple.voltskiya.custom_mobs.nms.base;


import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public record NmsMobSupers<Self extends Entity>(
    Function<TeleportTransition, Entity> teleportTo,
    BiConsumer<MoverType, Vec3> move,
    Consumer<CompoundTag> load,
    Function<CompoundTag, Boolean> save,
    Function<CompoundTag, CompoundTag> saveWithoutId,
    Consumer<Entity.RemovalReason> remove
) {

}

