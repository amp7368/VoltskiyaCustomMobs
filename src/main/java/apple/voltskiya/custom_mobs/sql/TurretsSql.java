package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.turrets.EntityLocation;
import apple.voltskiya.custom_mobs.turrets.TurretBuilder;
import apple.voltskiya.custom_mobs.turrets.TurretMob;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.util.Pair;
import org.bukkit.Bukkit;
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
        turretUid = turretUid == -1 ? VerifyTurretsSql.currentTurretUid++ : turretUid;
        Location center = turretMob.getCenter();
        Vector facing = center.getDirection();
        synchronized (VerifyTurretsSql.syncDB) {
            final Material bow = turretMob.getBow();
            int bowId = bow == null ? DBUtils.getMyMaterialUid(Material.AIR) : DBUtils.getMyMaterialUid(bow);
            VerifyTurretsSql.database.setAutoCommit(false);
            Statement statement = VerifyTurretsSql.database.createStatement();
            statement.execute(String.format(
                    "REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" +
                            "                    %s, %s, %s)\n" +
                            "VALUES (%d,'%s',%f,%f,%f,%f,%f,%f,'%s','%s','%s',%d,%d,%f,'%s');\n",
                    TURRETS_TABLE, TURRET_UID, WORLD_UID, X, Y, Z,
                    X_FACING, Y_FACING, Z_FACING,
                    DURABILITY_ENTITY, BOW_ENTITY, REFILLED_ENTITY,
                    BOW, BOW_DURABILITY, HEALTH,TURRET_TYPE,
                    turretUid, center.getWorld().getUID().toString(), center.getX(), center.getY(), center.getZ(),
                    facing.getX(), facing.getY(), facing.getZ(),
                    turretMob.getDurabilityEntity().uuid.toString(), turretMob.getBowEntity().uuid.toString(), turretMob.getRefilledEntity().uuid.toString(),
                    bow == null ? null : bowId, turretMob.getBowDurability(), turretMob.getHealth(),turretMob.getTurretType().name()
            ));
            for (EntityLocation entityUid : turretMob.getTurretEntities()) {
                insertEntity(turretUid, statement, entityUid);
            }
            insertArrows(statement, turretMob.getUniqueId(), turretMob.getArrows());
            statement.close();
            VerifyTurretsSql.database.commit();
            VerifyTurretsSql.database.setAutoCommit(true);
            turretMob.setUniqueId(turretUid);
        }
    }

    private static void insertArrows(Statement statement, long turretUid, List<Pair<Material, Integer>> arrows) throws SQLException {
        statement.execute(String.format("DELETE FROM %s WHERE %s >= %d", ARROW_TABLE, ARROW_SLOT_INDEX, arrows.size()));
        for (int i = 0; i < arrows.size(); i++) {
            statement.execute(String.format("REPLACE INTO %s (%s, %s, %s, %s) VALUES (%d,%d,%d,%d)",
                    ARROW_TABLE,
                    TURRET_UID,
                    ARROW_SLOT_INDEX,
                    DBNames.MaterialNames.MATERIAL_UID,
                    ARROW_COUNT,
                    turretUid,
                    i,
                    DBUtils.getMyMaterialUid(arrows.get(i).getKey()),
                    arrows.get(i).getValue()
            ));
        }
    }

    private static void insertEntity(long turretUid, Statement statement, EntityLocation entityUid) throws SQLException {
        statement.execute(String.format(
                "INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%d,'%s',%f,%f,%f,%f,%f,%f) ON CONFLICT (%s,%s) DO NOTHING",
                TURRET_TO_ENTITY_TABLE,
                TURRET_UID, ENTITY_UID, X, Y, Z, X_FACING, Y_FACING, Z_FACING,
                turretUid,
                entityUid.uuid.toString(),
                entityUid.x,
                entityUid.y,
                entityUid.z,
                entityUid.xFacing,
                entityUid.yFacing,
                entityUid.zFacing,
                TURRET_UID, ENTITY_UID
        ));
    }


    public static List<TurretMob> getTurrets() throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            @NotNull Map<Long, List<EntityLocation>> entities = getEntities();
            @NotNull Map<Long, List<Pair<Material, Integer>>> arrows = getArrows();
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(
                    String.format("SELECT * FROM %s",
                            TURRETS_TABLE
                    )
            );
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
                List<Pair<Material, Integer>> arrow = arrows.getOrDefault(turretUid, new ArrayList<>());
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
                                arrow,
                                response.getInt(BOW),
                                response.getInt(BOW_DURABILITY),
                                turretUid,
                                TurretType.valueOf(response.getString(TURRET_TYPE))
                        )
                );
            }
            removeTurrets(turretsToRemove);
            statement.close();
            List<TurretMob> turretMobs = new ArrayList<>();
            for (TurretBuilder turretMob : turrets) turretMobs.add(turretMob.build());
            return turretMobs;
        }
    }

    private static Map<Long, List<Pair<Material, Integer>>> getArrows() throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Map<Long, List<Pair<Material, Integer>>> arrows = new HashMap<>();
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("SELECT * FROM %s\n" +
                    "ORDER BY %s,%s", ARROW_TABLE, TURRET_UID, ARROW_SLOT_INDEX));
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                arrows.putIfAbsent(turretUid, new ArrayList<>());
                List<Pair<Material, Integer>> arrow = arrows.get(turretUid);
                int index = response.getInt(ARROW_SLOT_INDEX);
                Material material = DBUtils.getMaterialName(response.getInt(DBNames.MaterialNames.MATERIAL_UID));
                int arrowCount = response.getInt(ARROW_COUNT);
                while (arrow.size() <= index) arrow.add(new Pair<>(Material.AIR, 0));
                arrow.set(index, new Pair<>(material, arrowCount));
            }
            return arrows;
        }
    }

    private static void removeTurrets(List<Long> turretsToRemove) throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            for (Long turretUid : turretsToRemove) {
                statement.execute(String.format("DELETE\n" +
                        "FROM %s\n" +
                        "WHERE %s = %d", TURRETS_TABLE, TURRET_UID, turretUid));
                statement.execute(String.format("DELETE\n" +
                        "FROM %s\n" +
                        "WHERE %s = %d", ARROW_TABLE, TURRET_UID, turretUid));
                statement.execute(String.format("DELETE\n" +
                        "FROM %s\n" +
                        "WHERE %s = %d", TURRET_TO_ENTITY_TABLE, TURRET_UID, turretUid));
            }
            statement.close();
        }
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
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format(
                    "SELECT * FROM %s",
                    TURRET_TO_ENTITY_TABLE
            ));
            Map<Long, List<EntityLocation>> entities = new HashMap<>();
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                entities.putIfAbsent(turretUid, new ArrayList<>());
                final UUID entityUid = UUID.fromString(response.getString(ENTITY_UID));
                if (Bukkit.getEntity(entityUid) == null) continue;
                entities.get(turretUid).add(
                        new EntityLocation(
                                entityUid,
                                response.getDouble(X),
                                response.getDouble(Y),
                                response.getDouble(Z),
                                response.getDouble(X_FACING),
                                response.getDouble(Y_FACING),
                                response.getDouble(Z_FACING)
                        )
                );
            }
            statement.close();
            return entities;
        }
    }

    public static void removeTurret(long uid) throws SQLException {
        removeTurrets(Collections.singletonList(uid));
    }
}