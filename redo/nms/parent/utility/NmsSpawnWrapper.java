package apple.voltskiya.custom_mobs.mobs.nms.parent.utility;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegisterConfigable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NmsSpawnWrapper<SelfEntity extends Entity & NmsUtility<SelfEntity>> {
    private final String name;
    private final String tag;
    private final EntityTypes.b<SelfEntity> create;
    private final EntityTypes<?> replacement;
    private EntityTypes<SelfEntity> entityTypes;

    public NmsSpawnWrapper(String name, EntityTypes.b<SelfEntity> create, EntityTypes<?> replacement) {
        this.name = name;
        this.tag = name;
        this.create = create;
        this.replacement = replacement;
    }

    /**
     * registers the IllagerExaminer as an entity
     */
    public void initialize() {
        entityTypes = NmsMobRegisterConfigable.registerEntityTypesStatic(registeredNameId(), create, replacement);
    }

    @NotNull
    private String registeredNameId() {
        return "minecraft" + ":" + name;
    }

    /**
     * spawns it
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     * @return the spawned
     */
    public SelfEntity spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();

        SelfEntity entity = create.create(entityTypes, world.getHandle());
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
        NBTTagCompound oldNbt = DecodeEntity.save(handle);
        spawn(location, oldNbt);
        event.setCancelled(true);
    }

    public String tag() {
        return this.tag;
    }

    public String name() {
        return this.name;
    }

    public EntityTypes<SelfEntity> entityTypes() {
        return entityTypes;
    }
}
