package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVex;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MicroMissleCluster {

    public static final double SPEED = 5.5;

    public static void spawnCluster(Location location, Location targetLocation, LivingEntity target) {
        Vector direction = targetLocation.toVector().subtract(location.toVector());
        direction.normalize();
        direction.multiply(0.3);

        Vector directionLeft = new Vector().copy(direction);
        directionLeft.setX(directionLeft.getX() * Math.cos(Math.toRadians(30)) - Math.sin(Math.toRadians(30)) * directionLeft.getZ());
        directionLeft.setZ(directionLeft.getX() * Math.sin(Math.toRadians(30)) + Math.cos(Math.toRadians(30)) * directionLeft.getZ());

        Vector directionRight = new Vector().copy(direction);
        directionRight.setX(directionRight.getX() * Math.cos(Math.toRadians(-30)) - Math.sin(Math.toRadians(-30)) * directionRight.getZ());
        directionRight.setZ(directionRight.getX() * Math.sin(Math.toRadians(-30)) + Math.cos(Math.toRadians(-30)) * directionRight.getZ());

        direction.multiply(SPEED);
        directionLeft.multiply(SPEED);
        directionRight.multiply(SPEED);

        String cmd = "summon vex " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + MicroMissleSpawnManager.SUMMON_MISSILE_VEX;
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            @NotNull Collection<? extends Entity> vexes = location.getWorld().getNearbyEntitiesByType(EntityType.VEX.getEntityClass(), location, 2);
            int i = 0;
            for (Entity entity : vexes) {
                Vector v;
                switch (i++) {
                    case 0:
                        v = directionLeft;
                        break;
                    case 1:
                        v = directionRight;
                        break;
                    case 2:
                        v = direction;
                        break;
                    default:
                        v = null;
                        break;
                }
                if (v == null) break;
                ((CraftVex) entity).getHandle().g(new BlockPosition(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ()));
                entity.setVelocity(v);
                ((Vex) entity).setTarget(target);
                ((Vex) entity).setCharging(true);
                System.out.println(v);
            }
        }, 1);
    }
}
