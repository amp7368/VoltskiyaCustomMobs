package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("warped_gremlin")
@CommandPermission("warped_gremlin_spawn")
public class WarpedGremlinSpawnCommand extends BaseCommand {
    public WarpedGremlinSpawnCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("spawn")
    public void spawn(Player player) {
        MobWarpedGremlin.spawn("hmmm", player.getWorld(), player.getLocation());
    }
}
