package apple.voltskiya.custom_mobs.tick.revive;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

class Reviver {
    private final UUID reviver;
    private final List<UUID> linkedMobs = new ArrayList<>();
    private final Random random = new Random();

    public Reviver(Entity reviver) {
        this.reviver = reviver.getUniqueId();
    }

    public UUID getUniqueId() {
        return reviver;
    }

    @Nullable
    public Entity getEntity() {
        return Bukkit.getEntity(reviver);
    }

    public synchronized void addMob(Entity newMob) {
        this.linkedMobs.add(newMob.getUniqueId());
    }

    public synchronized void kill() {
        int i = 1;
        final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
        for (UUID uuid : linkedMobs) {
            Entity mob = Bukkit.getEntity(uuid);
            if (mob != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> kill(mob), (long) (i++ * random.nextDouble() * 30));
            }
        }
    }

    private void kill(Entity mob) {
        Location location = mob.getLocation();
        for (int i = 0; i < 20; i++) {
            double xi = random.nextDouble() - .5;
            double yi = random.nextDouble() * 2;
            double zi = random.nextDouble() - .5;
            location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 1, xi, yi, zi, 1);
        }
        location.getWorld().playSound(location, Sound.ITEM_TOTEM_USE, SoundCategory.HOSTILE,10,1.3f);
        mob.remove();
    }
}
