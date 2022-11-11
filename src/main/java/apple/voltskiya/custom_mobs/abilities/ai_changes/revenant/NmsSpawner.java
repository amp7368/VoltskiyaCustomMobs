package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.nbt.DecodeNBT;
import apple.voltskiya.mob_manager.listen.MMReSpawnResult;
import apple.voltskiya.mob_manager.listen.ReSpawnListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class NmsSpawner<E extends Entity> implements ReSpawnListener {

    private EntityType<E> entityType;
    private final String tag;

    public NmsSpawner(String tag) {
        this.tag = tag;
        this.registerReSpawnListener();
    }

    @Override
    public String getBriefTag() {
        return this.tag;
    }


    @Override
    public MMReSpawnResult doReSpawn(CreatureSpawnEvent event) {
        System.out.println("respawn");
        LivingEntity originalEntity = event.getEntity();
        Location location = originalEntity.getLocation();
        CompoundTag nbt = DecodeEntity.save(((CraftEntity) originalEntity).getHandle());
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
        E entity = getEntityFactory().create(getEntityType(), world);
        prepare(entity, location, nbt);
        return reSpawnResult(entity, world);
    }

    protected abstract EntityFactory<E> getEntityFactory();


    protected void prepare(E entity, Location location, CompoundTag nbt) {
        if (entity instanceof NmsMob nms)
            nms.prepare();
        DecodeNBT.removeKey(nbt, "UUID");
        DecodeEntity.load(entity, nbt);
        entity.getBukkitEntity().teleport(location);
    }

    public abstract EntityType<E> getEntityType();

    protected String registeredNameId() {
        return "minecraft:" + this.getBriefTag();
    }
}
