package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.sql.DBUtils;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretBuilder {
    private final Location location;
    private final double health;
    private final List<DBItemStack> arrows;
    private final long uid;
    private final long bowUid;
    private List<EntityLocation> turretEntities = new ArrayList<>();
    private EntityLocation durabilityEntity;
    private Entity durabilityEntityReal;
    private EntityLocation refilledEntity;
    private EntityLocation bowEntity;
    private final TurretType turretType;

    /**
     * create a new TurretMob and register it as a new turret
     *
     * @param player the player who spawned the turret
     */
    public TurretBuilder(Player player, TurretType turretType) {
        this.location = player.getLocation();
        this.arrows = new ArrayList<>();
        this.health = TurretMob.MAX_HEALTH;
        this.bowUid = -1;
        this.uid = -1;
        this.turretType = turretType;
    }

    public TurretBuilder(UUID worldUid, double x, double y, double z,
                         double facingX, double facingY, double facingZ,
                         List<EntityLocation> turretEntities,
                         EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                         double health,
                         List<DBItemStack> arrows,
                         long bowUid,
                         long uid,
                         TurretType turretType
    ) {
        this.turretType = turretType;
        final World world = Bukkit.getWorld(worldUid);
        this.location = new Location(world, x, y, z);
        this.location.setDirection(new Vector(facingX, facingY, facingZ));
        this.turretEntities = turretEntities;
        this.durabilityEntityReal = Bukkit.getEntity(durabilityEntity.uuid);
        this.durabilityEntity = durabilityEntity;
        this.refilledEntity = refilledEntity;
        this.bowEntity = bowEntity;
        this.health = health;
        this.arrows = arrows;
        this.bowUid = bowUid;
        this.uid = uid;
    }


    public synchronized void addEntity(Entity e) {
        e.addScoreboardTag(TurretMob.TURRET_TAG);
        this.turretEntities.add(new EntityLocation(e,
                -this.location.getX(), -this.location.getY(), -this.location.getZ()));
    }

    public synchronized void addDurabilityEntity(Entity e) {
        this.durabilityEntity = new EntityLocation(e,
                -this.location.getX(), -this.location.getY(), -this.location.getZ());
        this.durabilityEntityReal = e;
        addEntity(e);
    }

    public synchronized void addRefilledEntity(Entity e) {
        this.refilledEntity = new EntityLocation(e,
                -this.location.getX(), -this.location.getY(), -this.location.getZ());
        addEntity(e);
    }

    public synchronized void addBowEntity(Entity e) {
        this.bowEntity = new EntityLocation(e,
                -this.location.getX(), -this.location.getY(), -this.location.getZ());
        addEntity(e);
    }

    public TurretMob build() throws SQLException {
        UUID world = location.getWorld().getUID();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double facingX = location.getDirection().getX();
        double facingY = location.getDirection().getY();
        double facingZ = location.getDirection().getZ();

        if (uid < 0) {
            return new TurretMob(world, x, y, z,
                    facingX,
                    facingY,
                    facingZ,
                    turretEntities,
                    durabilityEntityReal,
                    durabilityEntity,
                    refilledEntity,
                    bowEntity,
                    health,
                    arrows,
                    new ItemStack(Material.AIR),
                    DBUtils.getAirItemStack(),
                    turretType
            );
        } else {
            final ItemStack bow = DBUtils.getItemStack(bowUid);
            return new TurretMob(world, x, y, z,
                    facingX,
                    facingY,
                    facingZ,
                    turretEntities,
                    durabilityEntity,
                    refilledEntity,
                    bowEntity,
                    health,
                    arrows,
                    bow,
                    bowUid,
                    uid,
                    turretType
            );
        }
    }
}
