package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FireFangsSpell implements Runnable {
    private final List<Location> locations = new ArrayList<>();
    private final FireFangs.FangsType type;
    private final List<Vector> directions = new ArrayList<>();
    private int ticksToLive;
    private final int fireLength;

    public FireFangsSpell(EntityInsentient me, FireFangs.FangsType type) {
        Location mainLocation = me.getBukkitEntity().getLocation();
        Vector mainDirection = mainLocation.getDirection().normalize().multiply(type.getStep());
        this.type = type;
        this.ticksToLive = Math.max(1, Math.min(100, (int) (type.getRange() / type.getStep())));
        this.fireLength = type.getFireLength();
        switch (this.type) {
            case TRIPLE:
            case BLUE_TRIPLE:
                directions.add(VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(), mainDirection.getY(), Math.toRadians(30)));
                directions.add(VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(), mainDirection.getY(), Math.toRadians(-30)));
            case NORMAL:
            case BLUE_NORMAL:
                directions.add(mainDirection);
        }
        for (Vector ignored : directions) {
            locations.add(mainLocation.clone());
        }
    }

    @Override
    public void run() {
        for (int locationIndex = 0; locationIndex < locations.size(); locationIndex++) {
            Location location = this.locations.get(locationIndex);
            Vector direction = this.directions.get(locationIndex);
            location.add(direction);
            Material blockTypeHere = location.getBlock().getType();
            if (blockTypeHere.isAir()) {
                // go down
                int downAmount = 0;
                while (location.add(0, -1, 0).getBlock().getType().isAir() && downAmount++ != 5) ;
                // we're at ground
                location.add(0, 1, 0);
            } else {
                // go up
                int upAmount = 0;
                while (!location.add(0, 1, 0).getBlock().getType().isAir() && upAmount++ != 5) ;
                // we're at ground
            }

            location.getWorld().spawn(location, EvokerFangs.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (evokerFangs -> {
            }));
            final Block blockAt = location.getWorld().getBlockAt(location);
            Material oldType = blockAt.getType(); // might be cave air (idk how it's different)
            if (oldType.isAir() || oldType == Material.SNOW) {
                blockAt.setType(type.isBlue() ? Material.SOUL_FIRE : Material.FIRE,false);

                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> blockAt.setType(oldType), this.fireLength);
            }
        }
        if (--this.ticksToLive != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        }
    }
}
