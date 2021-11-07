package apple.voltskiya.custom_mobs.trash.dungeon.gui.patrol;

import apple.voltskiya.custom_mobs.trash.dungeon.patrols.Patrol;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class PatrolGui extends InventoryGui {
    private final Player player;
    private final Patrol patrol;

    public PatrolGui(Player player, Patrol patrol) {
        this.player = player;
        this.patrol = patrol;
        addPage(new PatrolSettingsPage(this));
    }

    public Player getPlayer() {
        return player;
    }

    public Patrol getPatrol() {
        return patrol;
    }
}
