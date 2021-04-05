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
    private static final String TURRETS_CONTENT = String.format(
            "    %s              BIGINT    NOT NULL PRIMARY KEY,\n" +
                    "    %s        NCHAR(36) NOT NULL,\n" +
                    "    %s                DOUBLE    NOT NULL,\n" +
                    "    %s                DOUBLE    NOT NULL,\n" +
                    "    %s                DOUBLE    NOT NULL,\n" +
                    "    %s          DOUBLE    NOT NULL,\n" +
                    "    %s          DOUBLE    NOT NULL,\n" +
                    "    %s          DOUBLE    NOT NULL,\n" +
                    "    %s NCHAR(36) NOT NULL,\n" +
                    "    %s        NCHAR(36) NOT NULL,\n" +
                    "    %s   NCHAR(36) NOT NULL,\n" +
                    "    %s              INTEGER,\n" +
                    "    %s    INTEGER   NOT NULL,\n" +
                    "    %s           DOUBLE    NOT NULL",
            TurretNames.TURRET_UID,
            TurretNames.WORLD_UID,
            TurretNames.X,
            TurretNames.Y,
            TurretNames.Z,
            TurretNames.X_FACING,
            TurretNames.Y_FACING,
            TurretNames.Z_FACING,
            TurretNames.DURABILITY_ENTITY,
            TurretNames.BOW_ENTITY,
            TurretNames.REFILLED_ENTITY,
            TurretNames.BOW,
            TurretNames.BOW_DURABILITY,
            TurretNames.HEALTH
    );
    private static final String ARROWS_CONTENT = String.format(
            "    %s         BIGINT  NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    PRIMARY KEY (%s, %s)",
            TurretNames.TURRET_UID,
            TurretNames.ARROW_SLOT_INDEX,
            MaterialNames.MATERIAL_UID,
            TurretNames.ARROW_COUNT,
            TurretNames.TURRET_UID,
            TurretNames.ARROW_SLOT_INDEX

    );
    private static final String MATERIALS_CONTENT = String.format(
            "    %s  INTEGER     NOT NULL PRIMARY KEY,\n" +
                    "    %s VARCHAR(50) UNIQUE",
            MaterialNames.MATERIAL_UID,
            MaterialNames.MATERIAL_NAME
    );
    private static final String TURRET_TO_ENTITY_CONTENT = String.format(
            "    %s BIGINT    NOT NULL,\n" +
                    "    %s NCHAR(36) NOT NULL,\n" +
                    "    x          DOUBLE    NOT NULL,\n" +
                    "    y          DOUBLE    NOT NULL,\n" +
                    "    z          DOUBLE    NOT NULL,\n" +
                    "    x_facing    DOUBLE    NOT NULL,\n" +
                    "    y_facing    DOUBLE    NOT NULL,\n" +
                    "    z_facing    DOUBLE    NOT NULL,\n" +
                    "    UNIQUE (%s, %s),\n" +
                    "    PRIMARY KEY (%s, %s)",
            TurretNames.TURRET_UID, TurretNames.ENTITY_UID,
            TurretNames.TURRET_UID, TurretNames.ENTITY_UID,
            TurretNames.TURRET_UID, TurretNames.ENTITY_UID
    );
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( %s );";

    public static long currentTurretUid;
    public static long currentMaterialUid;

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
            statement.execute(String.format(CREATE_TABLE_FORMAT, TurretNames.TURRETS_TABLE, TURRETS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, MaterialNames.MATERIAL_TABLE, MATERIALS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, TurretNames.ARROW_TABLE, ARROWS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, TurretNames.TURRET_TO_ENTITY_TABLE, TURRET_TO_ENTITY_CONTENT));
            currentMaterialUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", MaterialNames.MATERIAL_UID, MaterialNames.MATERIAL_TABLE)).getInt(1);
            currentTurretUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", TurretNames.TURRET_UID, TurretNames.TURRETS_TABLE)).getInt(1);
            statement.close();
        }
    }

}
