package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.minecraft.MaterialUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FireFangsSpell implements PathfinderGoalShootSpell.Spell {
    protected final List<FireFangLine> fangLines = new ArrayList<>();
    protected final FireFangs.FangsType type;
    protected final EntityInsentient me;

    public FireFangsSpell(FireFangsCaster me, FireFangs.FangsType type) {
        this.me = me.getEntity();
        Location mainLocation = this.me.getBukkitEntity().getLocation();
        final EntityLiving goalTarget = this.me.getGoalTarget();
        Vector mainDirection;
        if (goalTarget == null) mainDirection = mainLocation.getDirection().normalize().multiply(type.getStep());
        else
            mainDirection = goalTarget.getBukkitEntity().getLocation().toVector().subtract(mainLocation.toVector()).normalize();
        this.type = type;
        int ticksToLive = Math.max(1, Math.min(100, (int) (type.getRange() / type.getStep())));
        int fireLength = type.getFireLength();
        switch (this.type) {
            case TRIPLE:
            case BLUE_TRIPLE:
                fangLines.add(new FireFangLine(
                        VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(), mainDirection.getY(), Math.toRadians(30)),
                        mainLocation.clone(),
                        ticksToLive,
                        fireLength
                ));
                fangLines.add(new FireFangLine(
                        VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(), mainDirection.getY(), Math.toRadians(-30)),
                        mainLocation.clone(),
                        ticksToLive,
                        fireLength
                ));
            case NORMAL:
            case BLUE_NORMAL:
            case TRIPLE_STRAIGHT:
            case BLUE_TRIPLE_STRAIGHT:
                fangLines.add(new FireFangLine(
                        mainDirection,
                        mainLocation.clone(),
                        ticksToLive,
                        fireLength
                ));
        }
    }


    @Override
    public void stateChoice() {
        for (final FireFangLine fireFangLine : this.fangLines) {
            Location location = fireFangLine.getLocation();
            Vector direction = fireFangLine.getDirection();
            fireFangLine.decrementLife();
            location.add(direction);
            Material blockTypeHere = location.getBlock().getType();
            if (MaterialUtils.isPassable(blockTypeHere) || blockTypeHere == Material.FIRE || blockTypeHere == Material.SOUL_FIRE) {
                // go down
                int downAmount = 0;
                while (MaterialUtils.isPassable(location.add(0, -1, 0).getBlock().getType()) && downAmount++ != 6) ;
                // we're at ground
                location.add(0, 1, 0);
            } else {
                // go up
                int upAmount = 0;
                while (!MaterialUtils.isPassable(location.add(0, 1, 0).getBlock().getType()) && upAmount++ != 2) ;
                // we're at ground
            }
            final Block blockAt = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Material oldType = blockAt.getType(); // might be cave air (idk how it's different)
            if (MaterialUtils.isPassable(oldType)) {
                location.getWorld().spawn(location, EvokerFangs.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (evokerFangs -> {
                }));
                blockAt.setType(type.isBlue() ? Material.SOUL_FIRE : Material.FIRE, false);
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> blockAt.setType(oldType), fireFangLine.getFireLength());
            } else if (oldType != Material.FIRE && oldType != Material.SOUL_FIRE) {
                fireFangLine.die();
            }
        }
        this.fangLines.removeIf(FireFangLine::isDead);
        if (this.shouldRun()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::stateChoice, 1);
        }
    }

    protected boolean shouldRun() {
        return this.fangLines.isEmpty();
    }
}
