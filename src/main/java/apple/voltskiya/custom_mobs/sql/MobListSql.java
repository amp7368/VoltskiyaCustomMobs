package apple.voltskiya.custom_mobs.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static apple.voltskiya.custom_mobs.sql.DBNames.*;

public class MobListSql {
    public static List<UUID> getMobs(String name) throws SQLException {
        synchronized (MobsSql.syncDB) {
            Statement statement = MobsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("SELECT %s.%s\n" +
                            "FROM %s\n" +
                            "         INNER JOIN %s ON %s.%s = %s.%s\n" +
                            "WHERE %s = '%s'",
                    MobNames.MOB_UID_TABLE, MobNames.MOB_UUID, MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, MobNames.MOB_UID_TABLE,
                    MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, MobNames.MOB_TYPE_UID, MobNames.MOB_UID_TABLE, MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_NAME, name));
            List<UUID> mobs = new ArrayList<>();
            while (response.next()) {
                mobs.add(UUID.fromString(response.getString(MobNames.MOB_UUID)));
            }
            statement.close();
            return mobs;
        }
    }

    public static void addMob(String name, UUID uuid) throws SQLException {
        synchronized (MobsSql.syncDB) {
            Statement statement = MobsSql.database.createStatement();
            statement.execute(String.format(String.format(
                    "INSERT INTO %s (%s, %s)\n" +
                            "VALUES ((SELECT %s FROM %s WHERE %s = '%%s'), '%%s') ON CONFLICT (%s,%s) DO NOTHING",
                    MobNames.MOB_UID_TABLE, MobNames.MOB_TYPE_UID, MobNames.MOB_UUID, MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, MobNames.MOB_TYPE_NAME, MobNames.MOB_TYPE_UID, MobNames.MOB_UUID),
                    name, uuid
            ));
            statement.close();
        }
    }

    public static void registerName(String name) throws SQLException {
        synchronized (MobsSql.syncDB) {
            Statement statement = MobsSql.database.createStatement();
            statement.execute(String.format("INSERT INTO %s (%s, %s)\n" +
                            "VALUES ((SELECT max(%s) FROM %s) + 1, '%s')\n" +
                            "ON CONFLICT (%s) DO NOTHING\n",
                    MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_NAME, MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, name, MobNames.MOB_TYPE_NAME));
            statement.close();
        }
    }

    public static void removeMob(UUID reviverUuid) {
        synchronized (MobsSql.syncDB) {
            Statement statement = null;
            try {
                statement = MobsSql.database.createStatement();
                statement.execute(String.format("DELETE FROM %s\n" +
                        "WHERE %s = '%s'", MobNames.MOB_UID_TABLE, MobNames.MOB_UUID, reviverUuid.toString()));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
    }
}
