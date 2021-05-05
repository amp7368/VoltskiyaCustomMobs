package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.testing.MobEndermanVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobMiscCustomModel;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobCart;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
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

    @Subcommand("misc")
    @CommandCompletion("@mob-schematics")
    public void spawnMisc(Player player, String name) {
        MobMiscCustomModel.spawn(player.getWorld(), player.getLocation(), name);
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

        @Subcommand("enderman_vex")
        public void spawnEndermanVex(Player player) {
            MobEndermanVex.spawn(player.getWorld(), player.getLocation());
        }

        @Subcommand("eye_plant")
        public void spawnEyePlant(Player player) {
            MobEyePlant.spawn(player.getLocation(), null);
        }

        @Subcommand("warped_gremlin")
        public void spawnGremlin(Player player) {
            MobWarpedGremlin.spawn(player.getLocation(), null);
        }

        @Subcommand("zombie_cow")
        public void spawnCow(Player player) {
            MobZombieCow.spawn("hmmm", player.getWorld(), player.getLocation());
        }

        @Subcommand("piglin_vex")
        public void spawnVex(Player player) {
            MobPiglinVex.spawn("hmmm", player.getWorld(), player.getLocation());
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
