package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.gui.InventoryGui;
import org.bukkit.entity.Player;

public class DungeonGui extends InventoryGui {
    private final Player player;
    private final DungeonScanner dungeonScanner;

    public DungeonGui(Player player, DungeonScanner dungeonScanner) {
        this.player = player;
        this.dungeonScanner = dungeonScanner;
        addPage(new DungeonPageSettings1(this),
                new DungeonPageMobConfigs(this),
                new DungeonPageMobs(this),
                new DungeonPageChests(this)
        );
        this.player.openInventory(this.getInventory());
    }

    public DungeonScanner getDungeonScanner() {
        return dungeonScanner;
    }
}
