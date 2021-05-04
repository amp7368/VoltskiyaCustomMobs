package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.turrets.TurretPlugin;
import org.bukkit.Material;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class VerifyTurretsSql {

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
                    "    %s              BIGINT,\n" +
                    "    %s           DOUBLE    NOT NULL,\n" +
                    "    %s           VARCHAR(15)    NOT NULL ",
            DBNames.TurretNames.TURRET_UID,
            DBNames.TurretNames.WORLD_UID,
            DBNames.TurretNames.X,
            DBNames.TurretNames.Y,
            DBNames.TurretNames.Z,
            DBNames.TurretNames.X_FACING,
            DBNames.TurretNames.Y_FACING,
            DBNames.TurretNames.Z_FACING,
            DBNames.TurretNames.DURABILITY_ENTITY,
            DBNames.TurretNames.BOW_ENTITY,
            DBNames.TurretNames.REFILLED_ENTITY,
            DBNames.TurretNames.BOW,
            DBNames.TurretNames.HEALTH,
            DBNames.TurretNames.TURRET_TYPE
    );
    private static final String ARROWS_CONTENT = String.format(
            "    %s         BIGINT  NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s TEXT NOT NULL,\n" +
                    "    PRIMARY KEY (%s, %s)",
            DBNames.TurretNames.TURRET_UID,
            DBNames.TurretNames.ARROW_SLOT_INDEX,
            DBNames.MaterialNames.MATERIAL_UID,
            DBNames.TurretNames.ARROW_COUNT,
            DBNames.TurretNames.ARROW_NBT,
            DBNames.TurretNames.TURRET_UID,
            DBNames.TurretNames.ARROW_SLOT_INDEX

    );
    private static final String MATERIALS_CONTENT = String.format(
            "    %s  INTEGER     NOT NULL PRIMARY KEY,\n" +
                    "    %s VARCHAR(50) UNIQUE",
            DBNames.MaterialNames.MATERIAL_UID,
            DBNames.MaterialNames.MATERIAL_NAME
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
            DBNames.TurretNames.TURRET_UID, DBNames.TurretNames.ENTITY_UID,
            DBNames.TurretNames.TURRET_UID, DBNames.TurretNames.ENTITY_UID,
            DBNames.TurretNames.TURRET_UID, DBNames.TurretNames.ENTITY_UID
    );
    private static final String ITEM_CONTENT = String.format(
            "    %s     BIGINT  NOT NULL PRIMARY KEY,\n" +
                    "    %s INTEGER NOT NULL,\n" +
                    "    %s   INTEGER NOT NULL,\n" +
                    "    %s   INTEGER NOT NULL\n",
            DBNames.ItemNames.ITEM_UID,
            DBNames.MaterialNames.MATERIAL_UID,
            DBNames.ItemNames.ITEM_COUNT,
            DBNames.ItemNames.DURABILITY
    );
    private static final String ENCHANTMENT_CONTENT = String.format(
            "   %s        BIGINT NOT NULL,\n" +
                    "    %s INTEGER NOT NULL," +
                    "    %s INTEGER NOT NULL, " +
                    " PRIMARY KEY (%s, %s)",
            DBNames.ItemNames.ITEM_UID,
            DBNames.ItemNames.ENCHANTMENT_UID,
            DBNames.ItemNames.ENCHANTMENT_LEVEL,
            DBNames.ItemNames.ITEM_UID,
            DBNames.ItemNames.ENCHANTMENT_UID

    );
    private static final String ENCHANTMENT_ENUM_CONTENT = String.format(
            "    %s   INTEGER NOT NULL PRIMARY KEY,\n" +
                    "    %s  VARCHAR(70) NOT NULL,\n" +
            "    %s  VARCHAR(70) NOT NULL",
            DBNames.ItemNames.ENCHANTMENT_UID,
            DBNames.ItemNames.ENCHANTMENT_NAMESPACE,
            DBNames.ItemNames.ENCHANTMENT_NAME
    );
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( %s );";
    public static long currentTurretUid;
    public static long currentMaterialUid;
    public static final Object syncDB = new Object();
    public static Connection database;
    public static long currentItemStackUid;
    public static long currentEnchantmentUid;

    /**
     * do any setup and make sure the static part of this class is completed
     */
    public static void initialize() {
        synchronized (syncDB) {
            VoltskiyaModule voltskiyaModule = TurretPlugin.get();
            try {
                Class.forName("org.sqlite.JDBC");
                // never close this because we're always using it
                database = DriverManager.getConnection("jdbc:sqlite:" + voltskiyaModule.getDataFolder() + File.separator + DBNames.TurretNames.DATABASE_NAME);
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
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.TurretNames.TURRETS_TABLE, TURRETS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.MaterialNames.MATERIAL_TABLE, MATERIALS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.TurretNames.ARROW_TABLE, ARROWS_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.TurretNames.TURRET_TO_ENTITY_TABLE, TURRET_TO_ENTITY_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.ItemNames.ITEM_TABLE, ITEM_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.ItemNames.ENCHANTMENT_TABLE, ENCHANTMENT_CONTENT));
            statement.execute(String.format(CREATE_TABLE_FORMAT, DBNames.ItemNames.ENCHANTMENT_ENUM_TABLE, ENCHANTMENT_ENUM_CONTENT));
            statement.execute(String.format("REPLACE INTO %s (%s, %s, %s, %s) VALUES (0, %d,0,0)",
                    DBNames.ItemNames.ITEM_TABLE,
                    DBNames.ItemNames.ITEM_UID,
                    DBNames.MaterialNames.MATERIAL_UID,
                    DBNames.ItemNames.ITEM_COUNT,
                    DBNames.ItemNames.DURABILITY,
                    DBUtils.getMyMaterialUid(Material.AIR)));
            currentMaterialUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", DBNames.MaterialNames.MATERIAL_UID, DBNames.MaterialNames.MATERIAL_TABLE)).getInt(1);
            currentItemStackUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", DBNames.ItemNames.ITEM_UID, DBNames.ItemNames.ITEM_TABLE)).getInt(1);
            currentEnchantmentUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", DBNames.ItemNames.ENCHANTMENT_UID, DBNames.ItemNames.ENCHANTMENT_ENUM_TABLE)).getInt(1);
            currentTurretUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", DBNames.TurretNames.TURRET_UID, DBNames.TurretNames.TURRETS_TABLE)).getInt(1);
            statement.close();
        }
    }

}
