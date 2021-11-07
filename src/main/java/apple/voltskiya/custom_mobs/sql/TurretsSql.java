package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.trash.old_turrets.OldTurretBuilder;
import apple.voltskiya.custom_mobs.trash.old_turrets.OldTurretMob;
import apple.voltskiya.custom_mobs.trash.old_turrets.OldTurretType;
import apple.voltskiya.custom_mobs.trash.old_turrets.gui.OldTurretTarget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.EntityLocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static apple.voltskiya.custom_mobs.sql.DBNames.TurretNames.*;

public class TurretsSql {
    public static void registerOrUpdate(OldTurretMob turretMob) throws SQLException {
        long turretUid = turretMob.getUniqueId();
        if (turretUid == -1) turretUid = VerifyTurretsSql.currentTurretUid++;
        turretMob.setUniqueId(turretUid);
        Location center = turretMob.getCenter();
        Vector facing = center.getDirection();
        synchronized (VerifyTurretsSql.syncDB) {
            final long bowId = turretMob.getBowId();
            VerifyTurretsSql.database.setAutoCommit(false);
            Statement statement = VerifyTurretsSql.database.createStatement();
            statement.execute(String.format(
                    "REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" +
                            "                    %s, %s, %s)\n" +
                            "VALUES (%d,'%s',%f,%f,%f,%f,%f,%f,'%s','%s','%s',%d,%f,'%s','%s');\n",
                    TURRETS_TABLE, TURRET_UID, WORLD_UID, X, Y, Z,
                    X_FACING, Y_FACING, Z_FACING,
                    DURABILITY_ENTITY, BOW_ENTITY, REFILLED_ENTITY,
                    BOW, HEALTH, TURRET_TYPE, TURRET_TARGET_TYPE,
                    turretUid, center.getWorld().getUID(), center.getX(), center.getY(), center.getZ(),
                    facing.getX(), facing.getY(), facing.getZ(),
                    turretMob.getDurabilityEntity().uuid.toString(), turretMob.getBowEntity().uuid.toString(), turretMob.getRefilledEntity().uuid.toString(),
                    bowId, turretMob.getHealth(), turretMob.getTurretType().name(), turretMob.getTargetType().name()
            ));
            for (EntityLocation entityUid : turretMob.getTurretEntities()) {
                insertEntity(turretUid, statement, entityUid);
            }
            insertArrows(statement, turretMob.getUniqueId(), turretMob.getArrows());
            statement.close();
            VerifyTurretsSql.database.commit();
            VerifyTurretsSql.database.setAutoCommit(true);
        }
    }

    private static void insertArrows(Statement statement, long turretUid, List<DBItemStack> arrows) throws SQLException {
        statement.execute(String.format("DELETE FROM %s WHERE %s = %d", ARROW_TABLE, TURRET_UID, turretUid));
        for (int i = 0; i < arrows.size(); i++) {
            statement.execute(String.format("REPLACE INTO %s (%s, %s, %s, %s, %s) VALUES (%d,%d,%d,%d,'%s')",
                    ARROW_TABLE,
                    TURRET_UID,
                    ARROW_SLOT_INDEX,
                    DBNames.MaterialNames.MATERIAL_UID,
                    ARROW_COUNT,
                    ARROW_NBT,
                    turretUid,
                    i,
                    DBUtils.getMyMaterialUid(arrows.get(i).type),
                    arrows.get(i).count,
                    arrows.get(i).nbt
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


    public static List<OldTurretMob> getTurrets() throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            @NotNull Map<Long, List<EntityLocation>> entities = getEntities();
            @NotNull Map<Long, List<DBItemStack>> arrows = getArrows();
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(
                    String.format("SELECT * FROM %s",
                            TURRETS_TABLE
                    )
            );
            List<OldTurretBuilder> turrets = new ArrayList<>();
            List<Long> turretsToRemove = new ArrayList<>();
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                final UUID durabilityEntityUUID = UUID.fromString(response.getString(DURABILITY_ENTITY));
                final UUID bowEnitiyUUID = UUID.fromString(response.getString(BOW_ENTITY));
                final UUID refilledEntityUUID = UUID.fromString(response.getString(REFILLED_ENTITY));

                EntityLocation durabilityEntity = getEntity(entities.get(turretUid), durabilityEntityUUID);
                EntityLocation bowEnitity = getEntity(entities.get(turretUid), bowEnitiyUUID);
                EntityLocation refilledEntity = getEntity(entities.get(turretUid), refilledEntityUUID);
                List<DBItemStack> arrow = arrows.getOrDefault(turretUid, new ArrayList<>());
                if (durabilityEntity == null || bowEnitity == null || refilledEntity == null) {
                    turretsToRemove.add(turretUid);
                    continue;
                }
                turrets.add(
                        new OldTurretBuilder(
                                UUID.fromString(response.getString(WORLD_UID)),
                                response.getDouble(X),
                                response.getDouble(Y),
                                response.getDouble(Z),
                                response.getDouble(X_FACING),
                                response.getDouble(Y_FACING),
                                response.getDouble(Z_FACING),
                                entities.get(turretUid),
                                durabilityEntity,
                                refilledEntity,
                                bowEnitity,
                                response.getDouble(HEALTH),
                                arrow,
                                response.getLong(BOW),
                                turretUid,
                                OldTurretType.valueOf(response.getString(TURRET_TYPE)),
                                OldTurretTarget.TurretTargetType.valueOf(response.getString(TURRET_TARGET_TYPE))
                        )
                );
            }
            removeTurrets(turretsToRemove);
            statement.close();
            List<OldTurretMob> turretMobs = new ArrayList<>();
            for (OldTurretBuilder turretMob : turrets) turretMobs.add(turretMob.build());
            return turretMobs;
        }
    }

    private static Map<Long, List<DBItemStack>> getArrows() throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Map<Long, List<DBItemStack>> arrows = new HashMap<>();
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("SELECT * FROM %s\n" +
                    "ORDER BY %s,%s", ARROW_TABLE, TURRET_UID, ARROW_SLOT_INDEX));
            while (response.next()) {
                final long turretUid = response.getLong(TURRET_UID);
                arrows.putIfAbsent(turretUid, new ArrayList<>());
                List<DBItemStack> arrow = arrows.get(turretUid);
                int index = response.getInt(ARROW_SLOT_INDEX);
                Material material = DBUtils.getMaterialName(response.getInt(DBNames.MaterialNames.MATERIAL_UID));
                int arrowCount = response.getInt(ARROW_COUNT);
                String nbt = response.getString(ARROW_NBT);
                while (arrow.size() <= index) arrow.add(new DBItemStack(Material.AIR, 0, ""));
                arrow.set(index, new DBItemStack(material, arrowCount, nbt));
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