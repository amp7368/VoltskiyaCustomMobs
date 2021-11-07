package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.turrets.mobs.TurretMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.EntityLocation;

import java.util.*;

public abstract class TurretMobSaveable {
    private DBLocation center;
    private ArrayList<UUID> entities;
    private UUID bowEntity;
    private UUID refilledEntity;
    private UUID durabilityEntity;
    private double health;
    private List<DBItemStack> arrows;
    private Vector facing;
    private List<EntityLocation> entitiesLocations;
    private HashMap<String, Integer> bowEnchantments;
    private Material bowMaterial;
    private int bowDurability;
    private String typeId;

    /**
     * bad constructor to use unless doing so through reflections (gson)
     */
    public TurretMobSaveable() {
    }

    public TurretMobSaveable(TurretMob turretMob) {
        entities = new ArrayList<>();
        for (Entity e : turretMob.getEntities()) entities.add(e.getUniqueId());
        this.bowEntity = turretMob.getBowEntity().getUniqueId();
        this.refilledEntity = turretMob.getRefilledEntity().getUniqueId();
        this.durabilityEntity = turretMob.getDurabilityEntity().getUniqueId();
        this.health = turretMob.getHealth();
        this.arrows = turretMob.getArrows();
        this.facing = turretMob.getFacing();
        this.center = new DBLocation(turretMob.getCenter());
        this.entitiesLocations = turretMob.getEntitiesLocations();
        this.bowEnchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> enchantment : turretMob.getEnchantmentLevels().entrySet()) {
            this.bowEnchantments.put(enchantment.getKey().getKey().getKey(), enchantment.getValue());
        }
        this.bowMaterial = turretMob.getBowMaterial();
        this.bowDurability = turretMob.getBowDurability();
        this.typeId = turretMob.getTypeId();
    }

    public abstract TurretMob build();

    public abstract String getUUID();

    public ArrayList<UUID> getEntities() {
        return entities;
    }

    public UUID getBowEntity() {
        return bowEntity;
    }

    public UUID getRefilledEntity() {
        return refilledEntity;
    }

    public UUID getDurabilityEntity() {
        return durabilityEntity;
    }

    public double getHealth() {
        return health;
    }

    public List<DBItemStack> getArrows() {
        return arrows;
    }

    public Vector getFacing() {
        return facing;
    }

    public List<EntityLocation> getEntitiesLocations() {
        return entitiesLocations;
    }

    public HashMap<Enchantment, Integer> getBowEnchantments() {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        for (Map.Entry<String, Integer> entry : bowEnchantments.entrySet()) {
            enchantments.put(Enchantment.getByKey(NamespacedKey.fromString(entry.getKey())), entry.getValue());
        }
        return enchantments;
    }

    public Material getBowMaterial() {
        return bowMaterial;
    }

    public int getBowDurability() {
        return bowDurability;
    }

    public String getTypeId() {
        return typeId;
    }

    public Location getCenter() {
        return center.toLocation();
    }

    private static class DBLocation {
        private final double x;
        private final double y;
        private final double z;
        private final double xf;
        private final double yf;
        private final double zf;
        private final UUID world;


        public DBLocation(Location l) {
            this.x = l.getX();
            this.y = l.getY();
            this.z = l.getZ();
            this.xf = l.getDirection().getX();
            this.yf = l.getDirection().getY();
            this.zf = l.getDirection().getZ();
            this.world = l.getWorld().getUID();
        }

        public Location toLocation() {
            return new Location(Bukkit.getWorld(world), x, y, z).setDirection(new Vector(xf, yf, zf));
        }
    }
}
