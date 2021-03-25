package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.turrets.EntityLocation;
import apple.voltskiya.custom_mobs.turrets.TurretMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.sql.Statement;

import static apple.voltskiya.custom_mobs.sql.DBNames.TurretNames.*;

public class TurretsSql {
    public static void registerOrUpdate(TurretMob turretMob) throws SQLException {
        long turretUid = turretMob.getUniqueId();
        turretUid = turretUid == -1 ? MobsSql.currentTurretUid++ : turretUid;
        Location center = turretMob.getCenter();
        Vector facing = center.getDirection();
        synchronized (MobsSql.syncDB) {
            final Material bow = turretMob.getBow();
            int bowId = bow == null ? -1 : registerOrUpdateMaterial(bow);
            MobsSql.database.setAutoCommit(false);
            Statement statement = MobsSql.database.createStatement();
            statement.execute(String.format(
                    "REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" +
                            "                    %s, %s)\n" +
                            "VALUES (%d,%f,%f,%f,%f,%f,%f,'%s','%s','%s',%d,%d,%f);\n",
                    TURRETS_TABLE, TURRET_UID, X, Y, Z,
                    X_FACING, Y_FACING, Z_FACING,
                    DURABILITY_ENTITY, BOW_ENTITY, REFILLED_ENTITY,
                    BOW, BOW_DURABILITY, HEALTH,
                    turretUid, center.getX(), center.getY(), center.getZ(),
                    facing.getX(), facing.getY(), facing.getZ(),
                    turretMob.getDurabilityEntity().uuid.toString(), turretMob.getBowEntity().toString(), turretMob.getRefilledEntity().toString(),
                    bow == null ? null : bowId, turretMob.getBowDurability(), turretMob.getHealth()
            ));
            for (EntityLocation entityUid : turretMob.getTurretEntities()) {
                insertEntity(turretUid, statement, entityUid);
            }
            insertEntity(turretUid,statement, turretMob.getBowEntity());
            insertEntity(turretUid,statement, turretMob.getRefilledEntity());
            insertEntity(turretUid,statement, turretMob.getDurabilityEntity());
            statement.close();
            MobsSql.database.commit();
            MobsSql.database.setAutoCommit(true);
            turretMob.setUniqueId(turretUid);
        }
    }

    private static boolean insertEntity(long turretUid, Statement statement, EntityLocation entityUid) throws SQLException {
        return statement.execute(String.format(
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

    private static int registerOrUpdateMaterial(Material material) throws SQLException {
        synchronized (MobsSql.syncDB) {
            Statement statement = MobsSql.database.createStatement();
            statement.execute(String.format(
                    "INSERT INTO %s (%s, %s)\n" +
                            "VALUES (%d, %s) ON CONFLICT (%s) DO NOTHING",
                    DBNames.MaterialNames.MATERIAL_TABLE, DBNames.MaterialNames.MATERIAL_UID, DBNames.MaterialNames.MATERIAL_NAME,
                    MobsSql.currentMaterialUid++, material.name(), DBNames.MaterialNames.MATERIAL_NAME
            ));
            MobsSql.currentMaterialUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", DBNames.MaterialNames.MATERIAL_UID, DBNames.MaterialNames.MATERIAL_TABLE)).getInt(1);
            int id = statement.executeQuery(String.format("SELECT %s FROM %s WHERE %s = %s",
                    DBNames.MaterialNames.MATERIAL_UID, DBNames.MaterialNames.MATERIAL_TABLE, DBNames.MaterialNames.MATERIAL_NAME, material.name()
            )).getInt(1);
            statement.close();
            return id;
        }
    }
}