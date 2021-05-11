package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("dungeon")
@CommandPermission("dungeon.create")
public class DungeonCommand extends BaseCommand {
    private final Map<UUID, DungeonScanner> playerDungeonScanners = new HashMap<>();

    public DungeonCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("dungeon-scanners", DungeonScanner::getSchematics);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("dungeon-instances", DungeonScanned::getSchematics);
    }

    @Subcommand("load")
    public class Load extends BaseCommand {
        @Subcommand("scanner")
        @CommandCompletion("@dungeon-scanners|name")
        public void loadScanner(Player player, @Single String dungeonScannerName) {
            DungeonScanner scanner;
            scanner = new DungeonScanner(dungeonScannerName);
            playerDungeonScanners.put(player.getUniqueId(), scanner);
            if (scanner.wasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon scanner " + dungeonScannerName + " from the database");
            } else {
                player.sendMessage(ChatColor.AQUA + "This dungeon scanner did not previously exist. Load a different scanner if you do not wish to create a new one");
            }
        }

        @Subcommand("dungeon")
        @CommandCompletion("@dungeon-instances|name")
        public void loadDungeon(Player player, @Single String dungeonInstanceName) {
            @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
            if (scanner == null) {
                player.sendMessage("Please load a dungeon scanner before attempting this");
                return;
            }
            scanner.loadDungeonInstance(dungeonInstanceName);
        }
    }

    @Subcommand("pos1")
    public void pos1(Player player) {
        @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
        if (scanner == null) {
            player.sendMessage("Please load a dungeon scanner before attempting this");
            return;
        }
        final Location location = player.getLocation();
        scanner.pos1(location);
        player.sendMessage(ChatColor.AQUA + String.format("pos1 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Subcommand("pos2")
    public void pos2(Player player) {
        @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
        if (scanner == null) {
            player.sendMessage("Please load a dungeon scanner before attempting this");
            return;
        }
        final Location location = player.getLocation();
        scanner.pos2(location);
        player.sendMessage(ChatColor.AQUA + String.format("pos2 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Subcommand("gui")
    public void gui(Player player) {
        @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
        if (scanner == null) {
            player.sendMessage("Please load a dungeon scanner before attempting this");
            return;
        }
        scanner.gui(player);
    }

    @Subcommand("scan")
    public class Scan extends BaseCommand {
        @Subcommand("dungeon")
        @CommandCompletion("name scanBlocks|true|false scanMobs|true|false scanChests|true|false")
        public void scan(Player player, @Single String dungeonName, @Optional Boolean scanBlocks, @Optional Boolean scanMobs, @Optional Boolean scanChests) {
            @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
            if (scanner == null || scanner.getPos1() == null || scanner.getPos2() == null) {
                player.sendMessage("Please load a dungeon scanner and set pos1 and pos2 before attempting this");
                return;
            }
            scanner.scanDungeon(dungeonName, scanBlocks, scanMobs, scanChests);
        }

        @Subcommand("mob_config")
        public void mobConfig(Player player) {
            @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
            if (scanner == null) {
                player.sendMessage("Please load a dungeon scanner before attempting this");
                return;
            }
            try {
                scanner.scanMobConfig();
            } catch (IllegalArgumentException | IllegalStateException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }
}
