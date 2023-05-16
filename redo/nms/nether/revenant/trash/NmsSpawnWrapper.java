package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant.trash;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NmsSpawnWrapper<SelfEntity extends Entity> {

    private final String name;
    private final String tag;
    private final EntityType.Builder<SelfEntity> create;
    private final EntityType<?> replacement;
    private EntityType<SelfEntity> entityType;

    public NmsSpawnWrapper(String name, EntityType.Builder<SelfEntity> create,
        EntityType<?> replacement) {
        this.name = name;
        this.tag = name;
        this.create = create;
        this.replacement = replacement;
    }

    public void initialize() {
        entityType = NmsMobRegisterConfigable.registerEntityTypeStatic(registeredNameId(), create,
            replacement);
    }

    @NotNull
    private String registeredNameId() {
        return "minecraft" + ":" + name;
    }


    public SelfEntity spawn(Location location, CompoundTag oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();

        SelfEntity entity = create.create(entityType, world.getHandle());
        entity.prepare(location, oldNbt);
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        bukkitEntity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        bukkitEntity.addScoreboardTag(tag);
        world.getHandle().addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.NATURAL);
        return entity;
    }


    public void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        Entity handle = ((CraftEntity) event.getEntity()).getHandle();
        CompoundTag oldNbt = DecodeEntity.save(handle);
        spawn(location, oldNbt);
        event.setCancelled(true);
    }

    public String tag() {
        return this.tag;
    }

    public String name() {
        return this.name;
    }

    public EntityType<SelfEntity> entityType() {
        return entityType;
    }
}
