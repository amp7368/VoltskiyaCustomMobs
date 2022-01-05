package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.phys.Vec3D;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record NmsMobEntitySupers(
        Function<WorldServer, Entity> changeWorlds,
        BiConsumer<EnumMoveType, Vec3D> move,
        Consumer<NBTTagCompound> load,
        Function<NBTTagCompound, NBTTagCompound> save,
        Consumer<Entity.RemovalReason> remove
) {
    public Entity changeWorlds(WorldServer worldserver) {
        return changeWorlds.apply(worldserver);
    }

    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        move.accept(enummovetype, vec3d);
    }

    public void load(NBTTagCompound nbttagcompound) {
        load.accept(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return save.apply(nbttagcompound);
    }

    public void remove(Entity.RemovalReason removalReason) {
        remove.accept(removalReason);
    }
}
