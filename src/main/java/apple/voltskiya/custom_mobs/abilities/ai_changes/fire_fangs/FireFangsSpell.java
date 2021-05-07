package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public class FireFangsSpell implements Runnable {
    private final Location location;
    private final FireFangs.FangsType type;
    private final Vector direction;
    private int ticksToLive;
    private final int fireLength;

    public FireFangsSpell(EntityInsentient me, FireFangs.FangsType type) {
        this.location = me.getBukkitEntity().getLocation();
        this.direction = this.location.getDirection().normalize().multiply(type.getStep());
        this.type = type;
        this.ticksToLive = (int) (type.getRange() / type.getStep());
        this.fireLength = type.getFireLength();
    }

    @Override
    public void run() {
        this.location.add(this.direction);
        Material blockTypeHere = this.location.getBlock().getType();
        if (blockTypeHere.isAir()) {
            // go down
            int downAmount = 0;
            while (this.location.add(0, -1, 0).getBlock().getType().isAir() && downAmount++ != 5) ;
            // we're at ground
            this.location.add(0, 1, 0);
        } else {
            // go up
            int upAmount = 0;
            while (!this.location.add(0, 1, 0).getBlock().getType().isAir() && upAmount++ != 5) ;
            // we're at ground
        }

        if (this.type == FireFangs.FangsType.NORMAL) {
            this.location.getWorld().spawn(location, EvokerFangs.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (evokerFangs -> {
            }));
            final Block blockAt = this.location.getWorld().getBlockAt(this.location);
            Material oldType = blockAt.getType(); // might be cave air (idk how it's different)
            if (oldType.isAir() || oldType==Material.SNOW) {
                blockAt.setType(Material.FIRE);
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> blockAt.setType(oldType), fireLength);
            }
        }
        if (--this.ticksToLive != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
        }
    }
}
