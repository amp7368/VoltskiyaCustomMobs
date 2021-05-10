package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

/**
 * represents a way to scan an area to turn it into a dungeon
 */
public class DungeonScanner {
    private final Map<String, DungeonMobConfig> nameToMobConfig = new HashMap<>();
    private final Map<Material, DungeonChestConfig> chestConfig = new HashMap<>();
    private Location pos1 = null;
    private Location pos2 = null;

    public void pos1(Location location) {
        this.pos1 = location;
    }

    public void pos2(Location location) {
        this.pos2 = location;
    }

    public void scan() {

    }

    public void scanMobConfig() {
        if (pos1 == null || pos2 == null) {
            throw new IllegalStateException("The positions have not been set yet");
        }
        World world = pos1.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        String configName = null;
        Entity configEntity = null;
        for (Entity entity : entities) {
            for (String tag : entity.getScoreboardTags()) {
                if (tag.startsWith(DungeonMobConfig.PREFIX_TAG)) {
                    configName = tag.substring(DungeonMobConfig.PREFIX_TAG.length());
                    configEntity = entity;
                }
            }
        }
        if (configName == null) {
            throw new IllegalArgumentException("There is no entity with the tag " + DungeonMobConfig.PREFIX_TAG + " as a prefix");
        }
        DungeonMobConfig mobConfig = new DungeonMobConfig(configName);
        for (Entity entity : entities) {
            if (configEntity != entity) {
                // add the rest into the config
                mobConfig.add(new DungeonMobInfo(entity));
            }
        }
        nameToMobConfig.put(configName, mobConfig);
    }

    public void gui(Player player) {
        new DungeonGui(player, this);
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public List<DungeonMobConfig> getMobConfigs() {
        List<DungeonMobConfig> configs = new ArrayList<>(this.nameToMobConfig.values());
        configs.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        return configs;
    }
}
