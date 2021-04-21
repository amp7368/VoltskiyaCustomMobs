package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("custom_spawn")
@CommandPermission("custom_mob")
public class WarpedGremlinSpawnCommand extends BaseCommand {
    public WarpedGremlinSpawnCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("warped_gremlin")
    public void spawnGremlin(Player player) {
        MobWarpedGremlin.spawn("hmmm", player.getWorld(), player.getLocation());
    }

    @Subcommand("zombie_cow")
    public void spawnCow(Player player) {
        MobZombieCow.spawn("hmmm", player.getWorld(), player.getLocation());
    }
    @Subcommand("piglin_vex")
    public void spawnVex(Player player){
        MobPiglinVex.spawn("hmmm", player.getWorld(), player.getLocation());
    }
}
