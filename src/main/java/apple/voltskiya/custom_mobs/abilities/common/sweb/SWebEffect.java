package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SWebEffect {

    private final List<FallingBlock> webs;
    private final SWebConfig config;
    private final Location center;
    private int timeRemaining;

    public SWebEffect(SWebConfig config, Location center) {
        this.webs = generateWebs(center);
        this.config = config;
        this.center = center;
        this.timeRemaining = config.spellDuration;
    }

    @NotNull
    private List<FallingBlock> generateWebs(Location center) {
        Random random = new Random();
        World world = center.getWorld();
        List<FallingBlock> webs = new ArrayList<>();
        for (double x = -1; x <= 1; x++) {
            for (double z = -1; z <= 1; z++) {
                double xi = (random.nextDouble(.5) + .5) * x;
                double zi = (random.nextDouble(.5) + .5) * z;
                double yi = random.nextDouble(.3) - .15;
                Location loc = center.clone().add(xi, yi, zi);
                FallingBlock web = world.spawn(loc, FallingBlock.class, SpawnReason.CUSTOM, (falling) -> {
                    falling.setBlockData(Material.COBWEB.createBlockData());
                    falling.setGravity(false);
                    falling.setTicksLived(Integer.MAX_VALUE);
                    falling.setCancelDrop(true);
                    falling.setInvulnerable(true);
                });
                webs.add(web);
            }
        }
        return webs;
    }

    private void applyEffects() {
        @NotNull Collection<LivingEntity> caughtEntities = center.getNearbyLivingEntities(1.5, .5, 1.5);
        for (LivingEntity caughtEntity : caughtEntities) {
            Vector velocity = caughtEntity.getVelocity();
            velocity.setY(velocity.getY() * config.slowJump);
            velocity.setX(velocity.getX() * config.slowMove);
            velocity.setZ(velocity.getZ() * config.slowMove);
            caughtEntity.setVelocity(velocity);
        }

        timeRemaining--;
        if (timeRemaining > 0) {
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::applyEffects, 1);
        } else {
            removeSpiderwebs();
        }
    }

    private void removeSpiderwebs() {
        for (FallingBlock web : webs) {
            web.remove();
        }
    }

    public void start() {
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::applyEffects, 0);
    }
}
