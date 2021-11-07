package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nms.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.trash.aledar.MobAledar;
import apple.voltskiya.custom_mobs.trash.aledar.MobCart;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("custom_spawn")
@CommandPermission("custom_mob")
public class MobsSpawnCommand extends BaseCommand {

    public static final List<String> PRESET_NAMES = Arrays.stream(NmsModelConfig.ModelConfigName.values()).map(
            f -> f.getFile() + ".yml").collect(Collectors.toList());

    public MobsSpawnCommand() {
        final PaperCommandManager commandManager = VoltskiyaPlugin.get().getCommandManager();
        commandManager.registerCommand(this);
        commandManager.getCommandCompletions().registerCompletion("mob-schematics", MobsSpawnCommand::getSchematics);
    }


    @Subcommand("preset")
    public class Preset extends BaseCommand {

        @Subcommand("aledar_cart")
        public void spawnAledarCart(Player player) {
            MobAledar.spawn(player.getWorld(), player.getLocation());
        }

        @Subcommand("rideable_cart")
        public void spawnCart(Player player) {
            MobCart.spawn(player.getLocation(), null);
        }

        @Subcommand("nether_parasite")
        public void spawnParasite(Player player) {
            MobParasite.spawn(player.getLocation(), null, null);
        }


        @Subcommand("eye_plant")
        public void spawnEyePlant(Player player) {
            MobEyePlant.spawn(player.getLocation(), null);
        }

        @Subcommand("warped_gremlin")
        public void spawnGremlin(Player player) {
            MobWarpedGremlin.spawn(player.getLocation(), null);
        }
    }

    public static Collection<String> getSchematics(BukkitCommandCompletionContext context) {
        File folder = PluginNmsMobs.get().getModelDataFolder();
        return Arrays.stream(folder.list(
                (f, f2) ->
                        f2.endsWith(".yml") && !PRESET_NAMES.contains(f2) && f2.startsWith(context.getInput())
        )).map(fn -> fn.substring(0, fn.length() - 4)).collect(Collectors.toList());
    }

}
