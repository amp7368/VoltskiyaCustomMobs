package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.turrets.TurretMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static apple.voltskiya.custom_mobs.sql.DBNames.TurretNames.*;

public class TurretsSql {
    public static void registerOrUpdate(TurretMob turretMob) throws SQLException {
        long uid = turretMob.getUniqueId();
        uid = uid == -1 ? MobsSql.currentTurretUid++ : uid;
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
                    uid, center.getX(), center.getY(), center.getZ(),
                    facing.getX(), facing.getY(), facing.getZ(),
                    turretMob.getDurabilityEntity().toString(), turretMob.getBowEntity().toString(), turretMob.getRefilledEntity().toString(),
                    bow == null ? null : bowId, turretMob.getBowDurability(), turretMob.getHealth()
            ));
            for (UUID entity : turretMob.getTurretEntities()) {
                statement.execute(String.format(
                        "INSERT INTO %s (%s,%s) VALUES (%d,'%s')",
                        TURRET_TO_ENTITY_TABLE,
                        TURRET_UID, ENTITY_UID,
                        uid,
                        entity.toString()
                ));
            }
            statement.close();
            MobsSql.database.commit();
            MobsSql.database.setAutoCommit(true);
        }
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