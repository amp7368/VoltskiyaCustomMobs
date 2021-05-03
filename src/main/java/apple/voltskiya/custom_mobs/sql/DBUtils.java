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

import static apple.voltskiya.custom_mobs.sql.DBNames.ItemNames;
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
            response = statement.executeQuery(String.format("SELECT * FROM %s \n" +
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
                                new NamespacedKey(
                                        response.getString(ItemNames.ENCHANTMENT_NAMESPACE),
                                        response.getString(ItemNames.ENCHANTMENT_NAME)
                                ) // idk how to turn a string into a namespaced key besides this
                        ),
                        response.getInt(ItemNames.ENCHANTMENT_LEVEL)));
            }
            statement.close();
            final Material material = getMaterialName(materialUid);
            if (durability >= material.getMaxDurability()) {
                return new ItemStack(Material.AIR);
            } else {
                ItemStack itemStack = new ItemStack(material, itemCount);
                for (Pair<Enchantment, Integer> enchantment : enchantments) {
                    itemStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
                }
                ItemMeta data = itemStack.getItemMeta();
                if (data instanceof Damageable) {
                    ((Damageable) data).setDamage(durability);
                }
                itemStack.setItemMeta(data);
                return itemStack;
            }
        }
    }

    public static long getAirItemStack() {
        return ItemNames.AIR_ITEM_STACK_ID;
    }

    public static long getItemUid(ItemStack bow) throws SQLException {
        if (bow == null || bow.getType().isAir()) return getAirItemStack();
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            int durability = ((Damageable) bow.getItemMeta()).getDamage();
            long currentItemStackUid = VerifyTurretsSql.currentItemStackUid++;
            statement.execute(String.format(
                    "INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, %d, %d, %d)",
                    ItemNames.ITEM_TABLE,
                    ItemNames.ITEM_UID, MATERIAL_UID, ItemNames.ITEM_COUNT, ItemNames.DURABILITY,
                    currentItemStackUid, DBUtils.getMyMaterialUid(bow.getType()), bow.getAmount(), durability
            ));
            Map<Enchantment, Integer> enchantments = bow.getEnchantments();
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                statement.execute(String.format(
                        "INSERT INTO %s (%s, %s, %s)\n" +
                                "VALUES (%d, %d, %d)\n",
                        ItemNames.ENCHANTMENT_TABLE, ItemNames.ITEM_UID, ItemNames.ENCHANTMENT_UID, ItemNames.ENCHANTMENT_LEVEL,
                        currentItemStackUid, DBUtils.getMyEnchantmentUid(enchantment.getKey()), enchantment.getValue()
                ));
            }
            statement.close();
            return currentItemStackUid;
        }
    }

    private static long getMyEnchantmentUid(Enchantment enchantmentName) throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            ResultSet response = statement.executeQuery(String.format(
                    "SELECT %s\n" +
                            "FROM %s \n" +
                            "WHERE %s = '%s'\n" +
                            "AND %s = '%s'",
                    ItemNames.ENCHANTMENT_UID, ItemNames.ENCHANTMENT_ENUM_TABLE,
                    ItemNames.ENCHANTMENT_NAMESPACE, enchantmentName.getKey().getNamespace(),
                    ItemNames.ENCHANTMENT_NAME, enchantmentName.getKey().getKey()
            ));
            if (response == null || response.isClosed()) {
                // create the item
                final long currentEnchantmentUid = VerifyTurretsSql.currentEnchantmentUid++;
                statement.execute(String.format(
                        "INSERT INTO %s (%s, %s, %s) " +
                                "VALUES (%d, '%s', '%s')",
                        ItemNames.ENCHANTMENT_ENUM_TABLE, ItemNames.ENCHANTMENT_UID, ItemNames.ENCHANTMENT_NAMESPACE, ItemNames.ENCHANTMENT_NAME,
                        currentEnchantmentUid, enchantmentName.getKey().getNamespace(), enchantmentName.getKey().getKey()
                ));
                return currentEnchantmentUid;
            } else {
                return response.getInt(1);
            }
        }
    }

    public static void removeItemUid(long itemId) throws SQLException {
        synchronized (VerifyTurretsSql.syncDB) {
            Statement statement = VerifyTurretsSql.database.createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE %s = %d", DBNames.ItemNames.ITEM_TABLE, DBNames.ItemNames.ITEM_UID, itemId));
            statement.close();
        }
    }
}
