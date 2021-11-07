package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfig;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import voltskiya.apple.utilities.util.chance.ChanceRolling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MobReviver<Config extends ReviverConfig> extends MobToTick<Config> {
    protected final ChanceRolling random;
    private final List<UUID> linkedMobs = new ArrayList<>();

    public MobReviver(Entity reviver, Config config) {
        super(reviver, config);
        random = new ChanceRolling(config.reviveChance);
    }

    @Override
    public void tick(int tickSpeed) {
        if (random.rollXTimes(tickSpeed)) {
            doAbility();
        }
    }

    protected abstract void doAbility();


    public void addLinkedMob(Entity newMob) {
        this.linkedMobs.add(newMob.getUniqueId());
    }

    public void kill() {
        int i = 1;
        final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
        for (UUID uuid : linkedMobs) {
            Entity mob = Bukkit.getEntity(uuid);
            if (mob != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> killLinkedMob(mob), (long) (i++ * random.random().nextDouble() * 30));
            }
        }
    }

    private void killLinkedMob(Entity mob) {
        Location location = mob.getLocation();
        for (int i = 0; i < 20; i++) {
            double xi = random.random().nextDouble() - .5;
            double yi = random.random().nextDouble() * 2;
            double zi = random.random().nextDouble() - .5;
            location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 1, xi, yi, zi, 1);
        }
        location.getWorld().playSound(location, Sound.ITEM_TOTEM_USE, SoundCategory.HOSTILE, 10, 1.3f);
        mob.remove();
    }
}
