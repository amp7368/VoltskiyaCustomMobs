package apple.voltskiya.custom_mobs.trash.dungeon.gui;

import apple.voltskiya.custom_mobs.trash.dungeon.product.Dungeon;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class DungeonGui extends InventoryGui {
    private final Player player;
    private final Dungeon dungeon;

    public DungeonGui(Player player, Dungeon dungeon) {
        this.player = player;
        this.dungeon = dungeon;
        addPage(new DungeonPageSettings1(this),
                new DungeonPageMobConfigs(this),
                new DungeonPageMobs(this),
                new DungeonPageChests(this)
        );
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Player getPlayer() {
        return player;
    }
}
