package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.gui.InventoryGui;
import org.bukkit.entity.Player;

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
