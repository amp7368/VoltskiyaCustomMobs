package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.MobTickPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static apple.voltskiya.custom_mobs.sql.DBNames.*;

public class VerifyMobsSql {
    private static final String MOB_UID_CONTENT = String.format("    %s INTEGER   NOT NULL,\n" +
            "    %s NCHAR(36) NOT NULL,\n" +
            "    PRIMARY KEY (%s, %s)," +
            "    FOREIGN KEY (%s) REFERENCES %s", MobNames.MOB_TYPE_UID, MobNames.MOB_UUID, MobNames.MOB_TYPE_UID, MobNames.MOB_UUID, MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_TO_TYPE_UID_TABLE);
    private static final String MOB_TYPE_TO_TYPE_UID = String.format("    %s INTEGER   NOT NULL,\n" +
            "    %s VARCHAR(40) NOT NULL UNIQUE,\n" +
            "    PRIMARY KEY (%s)", MobNames.MOB_TYPE_UID, MobNames.MOB_TYPE_NAME, MobNames.MOB_TYPE_UID);
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( %s );";

    public static Connection database;
    public static final Object syncDB = new Object();


    /**
     * do any setup and make sure the static part of this class is completed
     */
    public static void initialize() {
        synchronized (VerifyMobsSql.syncDB) {
            VoltskiyaModule voltskiyaModule = MobTickPlugin.get();
            try {
                Class.forName("org.sqlite.JDBC");
                // never close this because we're always using it
                VerifyMobsSql.database = DriverManager.getConnection("jdbc:sqlite:" + voltskiyaModule.getDataFolder() + File.separator + MobNames.DATABASE_NAME);
                VerifyMobsSql.verifyTables();
                voltskiyaModule.log(Level.INFO, "The sql database for mobs is connected");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                voltskiyaModule.log(Level.SEVERE, "The sql database for mobs is not properly set up");
                VerifyMobsSql.database = null;
            }
        }
    }

    private static void verifyTables() throws SQLException {
        synchronized (syncDB) {
            Statement statement = database.createStatement();
            statement.execute(String.format(CREATE_TABLE_FORMAT, MobNames.MOB_UID_TABLE, MOB_UID_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, MobNames.MOB_TYPE_TO_TYPE_UID_TABLE, MOB_TYPE_TO_TYPE_UID));
             statement.close();
        }
    }

}
