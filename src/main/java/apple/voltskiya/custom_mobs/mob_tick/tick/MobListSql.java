package apple.voltskiya.custom_mobs.mob_tick.tick;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mob_tick.MobTickPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static apple.voltskiya.custom_mobs.mob_tick.tick.DBNames.*;

public class MobListSql {
    private static final String MOB_UID_CONTENT = String.format("    %s INTEGER   NOT NULL,\n" +
            "    %s NCHAR(36) NOT NULL,\n" +
            "    PRIMARY KEY (%s, %s)," +
            "    FOREIGN KEY (%s) REFERENCES %s", MOB_TYPE_UID, MOB_UUID, MOB_TYPE_UID, MOB_UUID, MOB_TYPE_UID, MOB_TYPE_TO_TYPE_UID_TABLE);

    private static final String MOB_TYPE_TO_TYPE_UID = String.format("    %s INTEGER   NOT NULL,\n" +
            "    %s VARCHAR(40) NOT NULL UNIQUE,\n" +
            "    PRIMARY KEY (%s)", MOB_TYPE_UID, MOB_TYPE_NAME, MOB_TYPE_UID);
    private static Connection database;
    private static final Object syncDB = new Object();

    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( %s );";

    // set up the database file
    static {
        synchronized (syncDB) {
            VoltskiyaModule voltskiyaModule = MobTickPlugin.get();
            try {
                Class.forName("org.sqlite.JDBC");
                // never close this because we're always using it
                database = DriverManager.getConnection("jdbc:sqlite:" + voltskiyaModule.getDataFolder() + File.separator + DBNames.DATABASE_NAME);
                verifyTables();
                voltskiyaModule.log(Level.INFO, "The sql database for mobs is connected");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                voltskiyaModule.log(Level.SEVERE, "The sql database for mobs is not properly set up");
                database = null;
            }
        }
    }

    private static void verifyTables() throws SQLException {
        synchronized (syncDB) {
            Statement statement = database.createStatement();
            statement.execute(String.format(CREATE_TABLE_FORMAT, MOB_UID_TABLE, MOB_UID_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, MOB_TYPE_TO_TYPE_UID_TABLE, MOB_TYPE_TO_TYPE_UID));
            statement.close();
        }
    }

    /**
     * do any setup and make sure the static part of this class is completed
     */
    public static void initialize() {

    }

    public static List<UUID> getMobs(String name) throws SQLException {
        synchronized (syncDB) {
            Statement statement = database.createStatement();
            ResultSet response = statement.executeQuery(String.format("SELECT %s.%s\n" +
                            "FROM %s\n" +
                            "         INNER JOIN %s ON %s.%s = %s.%s\n" +
                            "WHERE %s = '%s'",
                    MOB_UID_TABLE, MOB_UUID, MOB_TYPE_TO_TYPE_UID_TABLE, MOB_UID_TABLE,
                    MOB_TYPE_TO_TYPE_UID_TABLE, MOB_TYPE_UID, MOB_UID_TABLE, MOB_TYPE_UID, MOB_TYPE_NAME, name));
            List<UUID> mobs = new ArrayList<>();
            while (response.next()) {
                mobs.add(UUID.fromString(response.getString(MOB_UUID)));
            }
            statement.close();
            return mobs;
        }
    }

    public static void addMob(String name, UUID uuid) throws SQLException {
        synchronized (syncDB) {
            Statement statement = database.createStatement();
            statement.execute(String.format(String.format(
                    "INSERT INTO %s (%s, %s)\n" +
                            "VALUES ((SELECT %s FROM %s WHERE %s = '%%s'), '%%s') ON CONFLICT (%s,%s) DO NOTHING",
                    MOB_UID_TABLE, MOB_TYPE_UID, MOB_UUID, MOB_TYPE_UID, MOB_TYPE_TO_TYPE_UID_TABLE, MOB_TYPE_NAME, MOB_TYPE_UID, MOB_UUID),
                    name, uuid
            ));
            statement.close();
        }
    }

    public static void registerName(String name) throws SQLException {
        synchronized (syncDB) {
            Statement statement = database.createStatement();
            statement.execute(String.format("INSERT INTO %s (%s, %s)\n" +
                            "VALUES ((SELECT max(%s) FROM %s) + 1, '%s')\n" +
                            "ON CONFLICT (%s) DO NOTHING\n",
                    MOB_TYPE_TO_TYPE_UID_TABLE, MOB_TYPE_UID, MOB_TYPE_NAME, MOB_TYPE_UID, MOB_TYPE_TO_TYPE_UID_TABLE, name, MOB_TYPE_NAME));
            statement.close();
        }
    }

    public static void removeMob(UUID reviverUuid)  {
        synchronized (syncDB) {
            try {
                Statement statement = database.createStatement();
                statement.execute(String.format("DELETE FROM %s\n" +
                        "WHERE %s = '%s'", MOB_UID_TABLE, MOB_UUID, reviverUuid.toString()));
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
