package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.dungeon.product.ScanDungeonOptions;
import apple.voltskiya.custom_mobs.dungeon.product.SpawnDungeonOptions;
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
    private final Map<UUID, Dungeon> playerDungeons = new HashMap<>();

    public DungeonCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("dungeon-scanners", DungeonScanner::getSchematics);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("dungeon-layouts", DungeonScanned::getSchematics);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("dungeon-dungeons", Dungeon::getSchematics);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        @Nullable Dungeon dungeon = playerDungeons.get(player.getUniqueId());
        if (dungeon == null) {
            player.sendMessage("Please load a dungeon before attempting this");
            return;
        }
        dungeon.gui(player);

    }

    @Subcommand("load")
    public class Load extends BaseCommand {
        @Subcommand("dungeon")
        @CommandCompletion("@dungeon-dungeons|name")
        public void loadDungeon(Player player, @Single String dungeonName) {
            Dungeon dungeon = new Dungeon(dungeonName);
            playerDungeons.put(player.getUniqueId(), dungeon);
            if (dungeon.wasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon " + dungeon.getName() + " from the database");
            } else {
                player.sendMessage(ChatColor.AQUA + "This dungeon did not previously exist. Load a different dungeon if you do not wish to create a new one");
            }
        }

        @Subcommand("scanner")
        @CommandCompletion("@dungeon-scanners|name")
        public void loadScanner(Player player, @Optional @Single String dungeonScannerName) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage("Please load a dungeon before attempting this");
                return;
            }
            DungeonScanner scanner = dungeon.loadScanner(dungeonScannerName);
            if (scanner.wasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon scanner " + scanner.getName() + " from the database");
            } else {
                player.sendMessage(ChatColor.AQUA + "This dungeon scanner did not previously exist. Load a different scanner if you do not wish to create a new one");
            }
        }

        @Subcommand("layout")
        @CommandCompletion("@dungeon-layouts|layoutName")
        public void loadLayout(Player player, @Optional @Single String dungeonInstanceName) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage("Please load a dungeon before attempting this");
                return;
            }
            DungeonScanned layout = dungeon.loadLayout(dungeonInstanceName);
            if (layout.wasLoaded()) {
                player.sendMessage(ChatColor.AQUA + "Loaded dungeon layout " + layout.getName() + " from the database");
            } else {
                player.sendMessage(ChatColor.AQUA + "This dungeon layout did not previously exist. Load a different layout if you do not wish to create a new one");
            }
        }
    }

    @Subcommand("spawn")
    public class Spawn extends BaseCommand {
        @Subcommand("all")
        @CommandCompletion("@dungeon-layouts|dungeonName")
        public void all(CommandSender player, String name) {
            Dungeon dungeon = new Dungeon(name);
            if (dungeon.wasLoaded()) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnAll();
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("blocks")
        @CommandCompletion("@dungeon-layouts|dungeonName")
        public void blocks(CommandSender player, String name) {
            Dungeon dungeon = new Dungeon(name);
            if (dungeon.wasLoaded()) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnBlocks(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("mobs")
        @CommandCompletion("@dungeon-layouts|dungeonName")
        public void mobs(CommandSender player, String name) {
            Dungeon dungeon = new Dungeon(name);
            if (dungeon.wasLoaded()) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnMobs(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("chest")
        @CommandCompletion("@dungeon-layouts|dungeonName")
        public void chest(CommandSender player, String name) {
            Dungeon dungeon = new Dungeon(name);
            if (dungeon.wasLoaded()) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnChests(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage(ChatColor.RED + "There is no dungeon '" + name + "'");
            }
        }

        @Subcommand("all")
        public void all(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon != null) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnAll();
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage("Please load a dungeon before attempting this");
            }
        }

        @Subcommand("blocks")
        public void blocks(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon != null) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnBlocks(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage("Please load a dungeon before attempting this");
            }
        }

        @Subcommand("mobs")
        public void mobs(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon != null) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnMobs(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage("Please load a dungeon before attempting this");
            }
        }

        @Subcommand("chest")
        public void chest(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon != null) {
                final SpawnDungeonOptions spawnDungeonOptions = new SpawnDungeonOptions();
                spawnDungeonOptions.setSpawnChests(true);
                dungeon.spawn(spawnDungeonOptions);
            } else {
                player.sendMessage("Please load a dungeon before attempting this");
            }
        }
    }

    @Subcommand("set")
    public class Set extends BaseCommand {
        @Subcommand("pos1")
        public void pos1(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage("Please load a dungeon before attempting this");
                return;
            }
            final Location location = player.getLocation();
            dungeon.getDungeonPlayerIO().pos1(location);
            player.sendMessage(ChatColor.AQUA + String.format("pos1 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }

        @Subcommand("pos2")
        public void pos2(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage("Please load a dungeon before attempting this");
                return;
            }
            final Location location = player.getLocation();
            dungeon.getDungeonPlayerIO().pos2(location);
            player.sendMessage(ChatColor.AQUA + String.format("pos2 set to (%d, %d, %d)", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }

        @Subcommand("center")
        public class Center extends BaseCommand {
            @Subcommand("real")
            public void realCenter(Player player) {
                Dungeon dungeon = playerDungeons.get(player.getUniqueId());
                dungeon.setDungeonLocation(player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Set " + dungeon.getName() + "'s location'");
            }

            @Subcommand("layout")
            public void layoutCenter(Player player) {
                Dungeon dungeon = playerDungeons.get(player.getUniqueId());
                if (dungeon == null || dungeon.getScanned() == null) {
                    player.sendMessage("Please load a dungeon and dungeon layout before attempting this");
                    return;
                }
                dungeon.getScanned().setCenter(player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Set " + dungeon.getName() + "'s layout location'");
            }
        }

    }

    @Subcommand("scan")
    public class Scan extends BaseCommand {
        @Subcommand("dungeon")
        @CommandCompletion("scanBlocks|true|false scanMobs|true|false scanChests|true|false")
        public void scanDungeon(Player player, @Optional Boolean scanBlocks, @Optional Boolean scanMobs, @Optional Boolean scanChests) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null || dungeon.getScanner() == null) {
                player.sendMessage("Please load a dungeon and dungeon scanner before attempting this");
                return;
            }
            try {
                dungeon.scanDungeon(new ScanDungeonOptions(scanBlocks, scanMobs, scanChests));
                player.sendMessage(ChatColor.AQUA + "The dungeon has been scanned");
            } catch (IllegalStateException e) {
                player.sendMessage("Please load a dungeon scanner and set pos1, pos2 before attempting this");
            }
        }

        @Subcommand("mob_config")
        public void mobConfig(Player player) {
            Dungeon dungeon = playerDungeons.get(player.getUniqueId());
            if (dungeon == null || dungeon.getScanner() == null) {
                player.sendMessage("Please load a dungeon before attempting this");
                return;
            }
            try {
                dungeon.scanMobConfig();
                player.sendMessage(ChatColor.AQUA + "The mob config has been scanned");
            } catch (IllegalStateException e) {
                player.sendMessage("Please load a dungeon scanner and set pos1, pos2 before attempting this");
            }
        }
    }
}
