package apple.voltskiya.custom_mobs.sql;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static apple.voltskiya.custom_mobs.sql.DBNames.MaterialNames.*;


public class DBUtils {
    private static final Map<Integer, Material> myToBlockName = new HashMap<>();
    private static final Map<Material, Integer> blockNameToMy = new HashMap<>();


    public static Material getMaterialName(int myBlockUid) throws SQLException {
        synchronized (myToBlockName) {
            Material real = myToBlockName.get(myBlockUid);
            if (real != null) return real;
        }
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format(String.format(
                    "SELECT %s FROM %s WHERE %s = %%d",
                    MATERIAL_NAME,
                    MATERIAL_TABLE,
                    MATERIAL_UID
            ), myBlockUid));
            final Material name = Material.valueOf(response.getString(MATERIAL_NAME));
            statement.close();
            myToBlockName.put(myBlockUid, name);
            return name;
        }
    }

    public static int getMyMaterialUid(@NotNull Material blockName) throws SQLException {
        synchronized (blockNameToMy) {
            Integer my = blockNameToMy.get(blockName);
            if (my != null) return my;
        }
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            statement.execute(String.format(String.format("INSERT INTO %s (%s, %s)\n" +
                            "VALUES ('%%s',(\n" +
                            "            SELECT ifnull(max(%s), 0)\n" +
                            "            FROM %s) + 1)\n" +
                            "ON CONFLICT (%s) DO NOTHING\n",
                    MATERIAL_TABLE,
                    MATERIAL_NAME,
                    MATERIAL_UID,
                    MATERIAL_UID,
                    MATERIAL_TABLE,
                    MATERIAL_NAME
            ), blockName));
            ResultSet response = statement.executeQuery(String.format(String.format(
                    "SELECT %s FROM %s WHERE %s = '%%s'",
                    MATERIAL_UID,
                    MATERIAL_TABLE,
                    MATERIAL_NAME
            ), blockName));
            int my = response.getInt(MATERIAL_UID);
            statement.close();
            blockNameToMy.put(blockName, my);
            return my;
        }
    }
}
