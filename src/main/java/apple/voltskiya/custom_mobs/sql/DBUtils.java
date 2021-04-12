package apple.voltskiya.custom_mobs.sql;

import apple.voltskiya.custom_mobs.util.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apple.voltskiya.custom_mobs.sql.DBNames.MaterialNames.*;
import static apple.voltskiya.custom_mobs.sql.DBNames.*;


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

    public static ItemStack getItemStack(long itemUid) throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format(
                    "SELECT * FROM %s WHERE %s = %d\n",
                    ItemNames.ITEM_TABLE, ItemNames.ITEM_UID, itemUid
            ));
            int materialUid = response.getInt(MATERIAL_UID);
            int itemCount = response.getInt(ItemNames.ITEM_COUNT);
            int durability = response.getInt(ItemNames.DURABILITY);
            response = statement.executeQuery(String.format("         SELECT * FROM %s \n" +
                            "         inner join %s on %s.%s = %s.%s\n" +
                            "WHERE %s = %d",
                    ItemNames.ENCHANTMENT_TABLE,
                    ItemNames.ENCHANTMENT_ENUM_TABLE,
                    ItemNames.ENCHANTMENT_TABLE,
                    ItemNames.ENCHANTMENT_UID,
                    ItemNames.ENCHANTMENT_ENUM_TABLE,
                    ItemNames.ENCHANTMENT_UID,
                    ItemNames.ITEM_UID,
                    itemUid
            ));
            List<Pair<Enchantment, Integer>> enchantments = new ArrayList<>();
            while (response.next()) {
                enchantments.add(new Pair<>(
                        Enchantment.getByKey(
                                NamespacedKey.minecraft(
                                        response.getString(ItemNames.ENCHANTMENT_NAME)
                                )
                        ),
                        response.getInt(ItemNames.ENCHANTMENT_LEVEL)));
            }
            statement.close();
            if (durability <= 0) {
                return new ItemStack(Material.AIR);
            } else {
                ItemStack itemStack = new ItemStack(getMaterialName(materialUid), itemCount);
                for (Pair<Enchantment, Integer> enchantment : enchantments) {
                    itemStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
                }
                ItemMeta data = itemStack.getItemMeta();
                if (data instanceof Damageable) {
                    ((Damageable) data).setDamage(durability);
                }
                return itemStack;
            }
        }
    }

    public static long getAirItemStack() {
        return ItemNames.AIR_ITEM_STACK_ID;
    }
}
