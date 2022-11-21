package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.mc.utilities.item.material.MaterialUtils;
import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsSpawner.FireFangsTypeConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class FireFangsSpell extends MMAbility<FireFangsTypeConfig> {

    protected final List<FireFangLine> fangLines = new ArrayList<>();

    public FireFangsSpell(MMSpawned mob, FireFangsTypeConfig config) {
        super(mob, config);
    }

    @Override
    protected void startAbility() {
        Location mainLocation = this.getLocation();
        final @Nullable LivingEntity goalTarget = this.getTarget();
        Vector mainDirection;
        if (goalTarget == null)
            mainDirection = mainLocation.getDirection().normalize().multiply(config.step);
        else
            mainDirection = goalTarget.getLocation().toVector()
                .subtract(mainLocation.toVector()).normalize();
        int ticksToLive = Math.max(1, Math.min(100, (int) (config.fireLength / config.step)));
        int fireLength = config.fireLength;
        if (config.isTriple) {
            fangLines.add(new FireFangLine(
                VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(),
                    mainDirection.getY(), Math.toRadians(30)), mainLocation.clone(),
                ticksToLive, fireLength));
            fangLines.add(new FireFangLine(
                VectorUtils.rotateVector(mainDirection.getX(), mainDirection.getZ(),
                    mainDirection.getY(), Math.toRadians(-30)), mainLocation.clone(),
                ticksToLive, fireLength));
        }
        fangLines.add(
            new FireFangLine(mainDirection, mainLocation.clone(), ticksToLive, fireLength));
        this.stateChoice();
    }

    @Override
    public void cleanUp(boolean isDead) {
    }


    public void stateChoice() {
        for (final FireFangLine fireFangLine : this.fangLines) {
            Location location = fireFangLine.getLocation();
            Vector direction = fireFangLine.getDirection();
            fireFangLine.decrementLife();
            location.add(direction);
            Material blockTypeHere = location.getBlock().getType();
            if (MaterialUtils.isPassable(blockTypeHere) || blockTypeHere == Material.FIRE
                || blockTypeHere == Material.SOUL_FIRE) {
                // go down
                int downAmount = 0;
                while (MaterialUtils.isPassable(location.add(0, -1, 0).getBlock().getType())
                    && downAmount++ != 6)
                    ;
                // we're at ground
                location.add(0, 1, 0);
            } else {
                // go up
                int upAmount = 0;
                while (!MaterialUtils.isPassable(location.add(0, 1, 0).getBlock().getType())
                    && upAmount++ != 2)
                    ;
                // we're at ground
            }
            final Block blockAt = location.getWorld()
                .getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Material oldType = blockAt.getType();
            if (MaterialUtils.isPassable(oldType)) {
                location.getWorld()
                    .spawn(location, EvokerFangs.class, CreatureSpawnEvent.SpawnReason.CUSTOM,
                        (evokerFangs -> {
                        }));
                blockAt.setType(config.isBlue ? Material.SOUL_FIRE : Material.FIRE, false);
                Bukkit.getScheduler()
                    .scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> blockAt.setType(oldType),
                        fireFangLine.getFireLength());
            } else if (oldType != Material.FIRE && oldType != Material.SOUL_FIRE) {
                fireFangLine.die();
            }
        }
        this.fangLines.removeIf(FireFangLine::isDead);
        if (this.hasLines()) {
            Bukkit.getScheduler()
                .scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::stateChoice, 1);
        } else
            this.finishAbility();
    }

    protected boolean hasLines() {
        return !this.fangLines.isEmpty();
    }


}
