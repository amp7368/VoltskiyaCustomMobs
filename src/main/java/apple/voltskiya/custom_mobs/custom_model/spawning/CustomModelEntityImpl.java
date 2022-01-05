package apple.voltskiya.custom_mobs.custom_model.spawning;

import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelEntityConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.VectorUtils;
import voltskiya.apple.utilities.util.data_structures.XYZ;

import java.util.UUID;

public class CustomModelEntityImpl<Config extends CustomModelEntityConfig> {
    protected UUID entityUUID;
    private transient Entity entity;
    private XYZ<Double> posVec;
    private XYZ<Double> facingVec;

    public CustomModelEntityImpl(Config config, Entity entity) {
        this.entity = entity;
        this.entityUUID = entity.getUniqueId();
        CustomModelDataEntity configData = config.getData();
        this.posVec = configData.getPosVec();
        this.facingVec = configData.getFacingVec();
    }

    public CustomModelEntityImpl() {
    }

    public UUID getUniqueId() {
        return entityUUID;
    }

    public void rotate(Location center, double angle) {
        XYZ<Double> centerPos = XYZ.from(center.toVector());
        Location newLocation = VectorUtils.rotate(posVec, facingVec, centerPos, Math.toRadians(angle));
        newLocation.setWorld(center.getWorld());
        Entity entity = getEntity();
        if (entity != null)
            entity.teleport(newLocation);
    }

    @Nullable
    public Entity getEntity() {
        if (entity == null) entity = Bukkit.getEntity(entityUUID);
        return entity;
    }
}
