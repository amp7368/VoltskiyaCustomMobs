package apple.voltskiya.custom_mobs.nms.parent.holder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record NmsMobEntitySupers(
        Function<ServerLevel, Entity> changeWorlds,
        BiConsumer<EnumMoveType, Vec3> move,
        Consumer<CompoundTag> load,
        Function<CompoundTag, CompoundTag> save,
        Consumer<Entity.RemovalReason> remove
) {
    public Entity changeWorlds(ServerLevel ServerLevel) {
        return changeWorlds.apply(ServerLevel);
    }

    public void move(EnumMoveType enummovetype, Vec3 Vec3) {
        move.accept(enummovetype, Vec3);
    }

    public void load(CompoundTag CompoundTag) {
        load.accept(CompoundTag);
    }

    public CompoundTag save(CompoundTag CompoundTag) {
        return save.apply(CompoundTag);
    }

    public void remove(Entity.RemovalReason removalReason) {
        remove.accept(removalReason);
    }
}
