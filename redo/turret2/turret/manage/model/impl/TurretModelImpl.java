package apple.voltskiya.custom_mobs.turret.manage.model.impl;

import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelConfig;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelEntityConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TurretModelImpl extends CustomModelImpl<TurretModelEntityConfig, TurretModelEntityImpl> {
    @Nullable
    public transient ArmorStand bow;
    @Nullable
    private transient Entity main;
    @Nullable
    private transient ArmorStand durability;
    @Nullable
    private transient ArmorStand refilled;
    private UUID mainUUID;
    private UUID durabilityUUID;
    private UUID refilledUUID;
    private UUID bowUUID;

    // for serialization
    public TurretModelImpl() {
    }

    public TurretModelImpl(TurretModelConfig model, Location location) {
        super(model, location);
    }

    private static void setHead(ArmorStand entity, Material material) {
        final EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(new ItemStack(material));
    }

    @Override
    public void addSpawnedEntity(TurretModelEntityImpl spawned) {
        if (spawned.isMain()) {
            this.main = spawned.getEntity();
            this.mainUUID = main.getUniqueId();
        }
        if (spawned.isDurability()) {
            this.durability = (ArmorStand) spawned.getEntity();
            this.durabilityUUID = durability.getUniqueId();
        }
        if (spawned.isRefilled()) {
            this.refilled = (ArmorStand) spawned.getEntity();
            this.refilledUUID = refilled.getUniqueId();
        }
        if (spawned.isBow()) {
            this.bow = (ArmorStand) spawned.getEntity();
            this.bowUUID = bow.getUniqueId();
        }
        super.addSpawnedEntity(spawned);
    }

    @Override
    protected TurretModelEntityImpl createNewImpl(TurretModelEntityConfig entityModel, @NotNull Entity spawned) {
        return new TurretModelEntityImpl(entityModel, spawned);
    }

    public void setDurabilityEntityHead(Material material) {
        setHead(getDurability(), material);
    }


    public void setRefilledEntityHead(Material material) {
        setHead(getRefilled(), material);
    }

    public void setBowEntity(Material material) {
        setHead(getBow(), material);
    }

    @Nullable
    public Entity getMain() {
        if (main == null) main = Bukkit.getEntity(mainUUID);
        return this.main;
    }

    private ArmorStand getRefilled() {
        if (refilled == null) {
            refilled = (ArmorStand) Bukkit.getEntity(refilledUUID);
        }
        return this.refilled;
    }

    private ArmorStand getDurability() {
        if (durability == null) {
            durability = (ArmorStand) Bukkit.getEntity(durabilityUUID);
        }
        return this.durability;
    }

    private ArmorStand getBow() {
        if (bow == null) {
            bow = (ArmorStand) Bukkit.getEntity(bowUUID);
        }
        return this.bow;
    }
}
