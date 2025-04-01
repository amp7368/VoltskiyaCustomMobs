package apple.voltskiya.custom_mobs.nms.base;

import apple.voltskiya.mob_manager.listen.ReSpawnListener;
import apple.voltskiya.mob_manager.listen.respawn.MMReSpawnResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public abstract class NmsSpawner<Self extends Entity, MC extends Entity> implements ReSpawnListener {


    @Override
    public String getBriefTag() {
        return this.getName();
    }

    public abstract String getName();

    public void init() {
        this.registerReSpawnListener();
    }

    @Override
    public MMReSpawnResult doReSpawn(Entity originalEntity) {
        Location location = originalEntity.getBukkitEntity().getLocation();
        CompoundTag nbt = originalEntity.saveWithoutId(new CompoundTag());
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
        Self entity = getEntityFactory().create(getEntityType(), world);
        prepare(entity, location, nbt);
        return reSpawnResult(entity, world);
    }

    protected abstract VoltEntityFactory<Self, MC> getEntityFactory();


    protected void prepare(Self entity, Location location, CompoundTag nbt) {
        if (entity instanceof INmsMob<?> nms)
            nms.prepare();
        nbt.remove("UUID");
        entity.load(nbt);
        entity.getBukkitEntity().teleport(location);
    }

    protected MobCategory getMobCategory() {
        return MobCategory.MONSTER;
    }

    public abstract EntityType<MC> getEntityType();
}
