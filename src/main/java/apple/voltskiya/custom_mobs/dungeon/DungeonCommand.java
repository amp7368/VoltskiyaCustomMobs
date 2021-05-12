package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
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
            scanner = new DungeonScanner(player, dungeonScannerName);
            playerDungeonScanners.put(player.getUniqueId(), scanner);
            if (scanner.wasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon scanner " + dungeonScannerName + " from the database");
            } else {
                player.sendMessage(ChatColor.AQUA + "This dungeon scanner did not previously exist. Load a different scanner if you do not wish to create a new one");
            }
        }

        @Subcommand("dungeon")
        @CommandCompletion("@dungeon-instances|layoutName")
        public void loadDungeon(Player player, @Single String dungeonInstanceName) {
            @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
            if (scanner == null) {
                player.sendMessage("Please load a dungeon scanner before attempting this");
                return;
            }
            if (scanner.loadDungeonInstance(dungeonInstanceName).isWasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon layout " + dungeonInstanceName + " from the database");
            } else {
                player.sendMessage("There is no dungeon layout with that name. If you wish to create a new one, scan one instead.");
            }
        }
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

    @Subcommand("spawn")
    public class Spawn extends BaseCommand {
        @Subcommand("all")
        @CommandCompletion("@dungeon-instances|dungeonName")
        public void all(CommandSender player, String name) {
            DungeonScanned dungeon = new DungeonScanned(name);
            if (dungeon.isWasLoaded()) {
                dungeon.spawnAll();
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("blocks")
        @CommandCompletion("@dungeon-instances|dungeonName")
        public void blocks(CommandSender player, String name) {
            DungeonScanned dungeon = new DungeonScanned(name);
            if (dungeon.isWasLoaded()) {
                dungeon.spawnBlocks();
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("mobs")
        @CommandCompletion("@dungeon-instances|dungeonName")
        public void mobs(CommandSender player, String name) {
            DungeonScanned dungeon = new DungeonScanned(name);
            if (dungeon.isWasLoaded()) {
                dungeon.spawnMobs();
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("chest")
        @CommandCompletion("@dungeon-instances|dungeonName")
        public void chest(CommandSender player, String name) {
            DungeonScanned dungeon = new DungeonScanned(name);
            if (dungeon.isWasLoaded()) {
                dungeon.spawnChests();
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }
    }

    @Subcommand("set")
    public class Set extends BaseCommand {
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

        @Subcommand("center")
        public class Center extends BaseCommand {
            @Subcommand("real")
            public void realCenter(Player player) {
                @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
                if (scanner == null || scanner.getDungeonInstance() == null) {
                    player.sendMessage("Please load a dungeon scanner and dungeon layout before attempting this");
                    return;
                }
                final Location location = player.getLocation();
                scanner.newDungeon(location);
                player.sendMessage(ChatColor.AQUA + "Set " + scanner.getDungeonInstance().getName() + " as a new dungeon. Ready to spawn.");
            }

            @Subcommand("layout")
            public void layoutCenter(Player player) {
                @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
                if (scanner == null) {
                    player.sendMessage("Please load a dungeon scanner before attempting this");
                    return;
                }
                final Location location = player.getLocation();
                scanner.center(location);
            }
        }

    }

    @Subcommand("scan")
    public class Scan extends BaseCommand {
        @Subcommand("dungeon")
        @CommandCompletion("layoutName scanBlocks|true|false scanMobs|true|false scanChests|true|false")
        public void scanDungeon(Player player, @Single String dungeonName, @Optional Boolean scanBlocks, @Optional Boolean scanMobs, @Optional Boolean scanChests) {
            @Nullable DungeonScanner scanner = playerDungeonScanners.get(player.getUniqueId());
            if (scanner == null || scanner.getPos1() == null || scanner.getPos2() == null || scanner.getCenter() == null) {
                player.sendMessage("Please load a dungeon scanner and set pos1, pos2, and layoutCenter before attempting this");
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
