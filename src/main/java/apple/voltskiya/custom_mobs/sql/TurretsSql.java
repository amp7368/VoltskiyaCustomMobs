package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.turrets.EntityLocation;
import apple.voltskiya.custom_mobs.turrets.TurretBuilder;
import apple.voltskiya.custom_mobs.turrets.TurretMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static apple.voltskiya.custom_mobs.sql.DBNames.TurretNames.*;

public class TurretsSql {
    public static void registerOrUpdate(TurretMob turretMob) throws SQLException {
        long turretUid = turretMob.getUniqueId();
        turretUid = turretUid == -1 ? VerifyMobsSql.currentTurretUid++ : turretUid;
        Location center = turretMob.getCenter();
        Vector facing = center.getDirection();
        synchronized (VerifyMobsSql.syncDB) {
            final Material bow = turretMob.getBow();
            int bowId = bow == null ? DBUtils.getMyMaterialUid(Material.AIR) : DBUtils.getMyMaterialUid(bow);
            VerifyMobsSql.database.setAutoCommit(false);
            Statement statement = VerifyMobsSql.database.createStatement();
            statement.execute(String.format(
                    "REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" +
                            "                    %s, %s)\n" +
                            "VALUES (%d,'%s',%f,%f,%f,%f,%f,%f,'%s','%s','%s',%d,%d,%f);\n",
                    TURRETS_TABLE, TURRET_UID, WORLD_UID, X, Y, Z,
                    X_FACING, Y_FACING, Z_FACING,
                    DURABILITY_ENTITY, BOW_ENTITY, REFILLED_ENTITY,
                    BOW, BOW_DURABILITY, HEALTH,
                    turretUid, center.getWorld().getUID().toString(), center.getX(), center.getY(), center.getZ(),
                    facing.getX(), facing.getY(), facing.getZ(),
                    turretMob.getDurabilityEntity().uuid.toString(), turretMob.getBowEntity().uuid.toString(), turretMob.getRefilledEntity().uuid.toString(),
                    bow == null ? null : bowId, turretMob.getBowDurability(), turretMob.getHealth()
            ));
            for (EntityLocation entityUid : turretMob.getTurretEntities()) {
                insertEntity(turretUid, statement, entityUid);
            }
            insertEntity(turretUid, statement, turretMob.getBowEntity());
            insertEntity(turretUid, statement, turretMob.getRefilledEntity());
            insertEntity(turretUid, statement, turretMob.getDurabilityEntity());
            statement.close();
            VerifyMobsSql.database.commit();
            VerifyMobsSql.database.setAutoCommit(true);
            turretMob.setUniqueId(turretUid);
        }
    }

    private static void insertEntity(long turretUid, Statement statement, EntityLocation entityUid) throws SQLException {
        statement.execute(String.format(
                "INSERT INTO %s (%s,%s,%s,%s,%s) VALUES (%d,'%s',%f,%f,%f) ON CONFLICT (%s,%s) DO NOTHING",
                TURRET_TO_ENTITY_TABLE,
                TURRET_UID, ENTITY_UID, X, Y, Z,
                turretUid,
                entityUid.uuid.toString(),
                entityUid.x,
                entityUid.y,
                entityUid.z,
                TURRET_UID, ENTITY_UID
        ));
    }


    public static List<TurretMob> getTurrets() throws SQLException {
        synchronized (VerifyMobsSql.syncDB) {
            @NotNull Map<Long, List<EntityLocation>> entities = getEntities();
            Statement statement = VerifyMobsSql.database.createStatement();
            ResultSet response = statement.executeQuery(
                    String.format("SELECT * FROM %s",
                            TURRETS_TABLE
                    ));
            List<TurretBuilder> turrets = new ArrayList<>();
            List<Long> turretsToRemove = new ArrayList<>();
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                final UUID durabilityEntityUUID = UUID.fromString(response.getString(DURABILITY_ENTITY));
                final UUID bowEnitiyUUID = UUID.fromString(response.getString(BOW_ENTITY));
                final UUID refilledEntityUUID = UUID.fromString(response.getString(REFILLED_ENTITY));

                EntityLocation durabilityEntity = getEntity(entities.get(turretUid), durabilityEntityUUID);
                EntityLocation bowEnitity = getEntity(entities.get(turretUid), bowEnitiyUUID);
                EntityLocation refilledEntity = getEntity(entities.get(turretUid), refilledEntityUUID);

                if (durabilityEntity == null || bowEnitity == null || refilledEntity == null) {
                    turretsToRemove.add(turretUid);
                    continue;
                }
                turrets.add(
                        new TurretBuilder(
                                UUID.fromString(response.getString(WORLD_UID)),
                                response.getDouble(X),
                                response.getDouble(Y),
                                response.getDouble(Z),
                                response.getDouble(X_FACING),
                                response.getDouble(Y_FACING),
                                response.getDouble(Z_FACING),
                                entities.get(turretUid),
                                durabilityEntity,
                                bowEnitity,
                                refilledEntity,
                                response.getDouble(HEALTH),
                                new ArrayList<>(),
                                response.getInt(BOW),
                                response.getInt(BOW_DURABILITY),
                                turretUid
                        )
                );
            }
            removeTurrets(turretsToRemove);
            statement.close();
            List<TurretMob> turretMobs = new ArrayList<>();
            for (TurretBuilder turretMob : turrets) turretMobs.add(turretMob.build());
            System.out.println(turrets.size());
            return turretMobs;
        }
    }

    private static void removeTurrets(List<Long> turretsToRemove) {
        // todo
    }

    @Nullable
    private static EntityLocation getEntity(List<EntityLocation> entities, UUID uuid) {
        for (EntityLocation entity : entities) {
            if (entity.uuid.equals(uuid)) return entity;
        }
        return null;
    }

    @NotNull
    private static Map<Long, List<EntityLocation>> getEntities() throws SQLException {
        synchronized (VerifyMobsSql.syncDB) {
            Statement statement = VerifyMobsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format(
                    "SELECT * FROM %s",
                    TURRET_TO_ENTITY_TABLE
            ));
            Map<Long, List<EntityLocation>> entities = new HashMap<>();
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                entities.putIfAbsent(turretUid, new ArrayList<>());
                entities.get(turretUid).add(
                        new EntityLocation(
                                UUID.fromString(response.getString(ENTITY_UID)),
                                response.getDouble(X),
                                response.getDouble(Y),
                                response.getDouble(Z)
                        )
                );
            }
            statement.close();
            return entities;
        }
    }
}