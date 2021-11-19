package apple.voltskiya.custom_mobs.custom_model.spawning;

import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelConfig;
import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelEntityConfig;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.VectorUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomModelImpl<Config extends CustomModelEntityConfig, Impl extends CustomModelEntityImpl<Config>> {
    public Location location;
    protected ArrayList<Impl> allEntities = new ArrayList<>();
    private transient CustomModelConfig<Config> modelConfig;

    // for serialization
    public CustomModelImpl() {
    }

    public CustomModelImpl(CustomModelConfig<Config> modelConfig, Location location) {
        this.modelConfig = modelConfig;
        this.location = location;
    }

    public void spawn() {
        for (Config entityModel : modelConfig.getParts()) {
            CustomModelDataEntity entity = entityModel.getData();
            Vector direction = VectorUtils.rotateVector(
                    entity.facingX,
                    entity.facingY,
                    entity.facingZ,
                    location.getYaw());
            Location entityLocation = location.clone()
                    .add(entity.x, entity.y, entity.z)
                    .setDirection(direction);
            Impl impl = spawnEntity(entityModel, entity.type, entity.nbt, entityLocation);
            addSpawnedEntity(impl);
        }
        modelConfig = null;
    }

    public void addSpawnedEntity(Impl impl) {
        allEntities.add(impl);
    }

    public Impl spawnEntity(Config entityModel, EntityType entityType, NBTTagCompound nbt, Location entityLocation) {
        @NotNull Entity spawned = entityLocation.getWorld().spawnEntity(entityLocation, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ((CraftEntity) spawned).getHandle().load(nbt);
        spawned.teleport(entityLocation);
        return createNewImpl(entityModel, spawned);
    }

    protected abstract Impl createNewImpl(Config entityModel, @NotNull Entity spawned);

    public void rotate(double angle) {
        for (Impl entity : allEntities) {
            entity.rotate(location, angle);
        }
    }

    public void kill() {
        for (Impl entity : allEntities) {
            entity.getEntity().remove();
        }
    }

    public List<Impl> getEntities() {
        return new ArrayList<>(this.allEntities);
    }
}
