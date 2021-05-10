package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("dungeon")
public class DungeonCommand extends BaseCommand {
    private final Map<UUID, DungeonScanner> playerDungeonScanners = new HashMap<>();

    public DungeonCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("pos1")
    public void pos1(Player player) {
        DungeonScanner scanner = playerDungeonScanners.computeIfAbsent(player.getUniqueId(), s -> new DungeonScanner());
        final Location location = player.getLocation();
        scanner.pos1(location);
        player.sendMessage(ChatColor.AQUA + String.format("pos1 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Subcommand("pos2")
    public void pos2(Player player) {
        DungeonScanner scanner = playerDungeonScanners.computeIfAbsent(player.getUniqueId(), s -> new DungeonScanner());
        final Location location = player.getLocation();
        scanner.pos2(location);
        player.sendMessage(ChatColor.AQUA + String.format("pos2 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Subcommand("scan")
    public void scan(Player player) {
        DungeonScanner scanner = playerDungeonScanners.computeIfAbsent(player.getUniqueId(), s -> new DungeonScanner());
        scanner.scan();
    }

    @Subcommand("gui")
    public void gui(Player player) {
        DungeonScanner scanner = playerDungeonScanners.computeIfAbsent(player.getUniqueId(), s -> new DungeonScanner());
        scanner.gui(player);
    }
}
