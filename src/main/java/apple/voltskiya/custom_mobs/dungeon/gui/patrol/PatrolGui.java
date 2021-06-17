package apple.voltskiya.custom_mobs.dungeon.gui.patrol;

import apple.voltskiya.custom_mobs.dungeon.patrols.Patrol;
import apple.voltskiya.custom_mobs.gui.InventoryGui;
import org.bukkit.entity.Player;

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
