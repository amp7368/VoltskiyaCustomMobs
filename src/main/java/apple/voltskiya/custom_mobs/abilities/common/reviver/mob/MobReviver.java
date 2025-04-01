package apple.voltskiya.custom_mobs.abilities.common.reviver.mob;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.common.reviver.config.ReviverConfig;
import apple.voltskiya.custom_mobs.abilities.common.reviver.dead.DeadRecordedMob;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import voltskiya.apple.utilities.minecraft.TagConstants;

public abstract class MobReviver<Config extends ReviverConfig> extends MMAbility<Config> {

    private static final long TIME_TO_RISE = 50;
    private static final double PARTICLE_RADIUS = .5;

    private final List<UUID> linkedMobs = new ArrayList<>();

    public MobReviver(MMSpawned mob, Config config) {
        super(mob, config);
    }

    public void linkMob(Entity newMob) {
        this.linkedMobs.add(newMob.getUniqueId());
    }

    protected void doReviveSummon(DeadRecordedMob reviveMe) {
        reviveMe.spawn(newMob -> dealWithSummonedMob(reviveMe, (Mob) newMob));
    }

    private void dealWithSummonedMob(DeadRecordedMob reviveMe, Mob newMob) {
        this.linkMob(newMob);

        // init mob
        double health = newMob.getAttribute(Attribute.MAX_HEALTH).getValue();
        newMob.setHealth(health);
        newMob.setAI(false);
        newMob.setInvulnerable(true);

        // cancel infinite revivals
        if (newMob.getScoreboardTags().contains("was_revived_1")) {
            newMob.addScoreboardTag("was_revived_2");
        } else {
            newMob.addScoreboardTag("was_revived_1");
        }

        Location reviveMeLocation = reviveMe.getLocation();
        reviveMeLocation.add(0, -2, 0);
        reviveMeLocation.setPitch(-55);
        newMob.teleport(reviveMeLocation);
        double interval = 3d / TIME_TO_RISE;
        for (int time = 0; time < TIME_TO_RISE; time++) {
            if (time % 3 == 0)
                VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
                    getLocation().getWorld()
                        .playSound(getLocation(), Sound.BLOCK_GRAVEL_BREAK, 6, 0.75f);
                }, time);
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
                Location newLocation = newMob.getLocation();
                particles(newLocation);
                newLocation.add(0, interval, 0);
                newMob.teleport(newLocation);
            }, time);
        }
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
            newMob.setAI(true);
            newMob.setInvulnerable(false);
            newMob.addScoreboardTag(TagConstants.LOOT_EMPTY);
            reviveMe.remove();
        }, TIME_TO_RISE);
    }


    private void particles(Location location) {
        double xi = location.getX();
        double yi = location.getY();
        double zi = location.getZ();
        for (int i = 0; i < 10; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * PARTICLE_RADIUS;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.nextDouble() * 2;
            location.getWorld().spawnParticle(Particle.WITCH, xi + x, yi + y, zi + z, 1);
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        int i = 1;
        final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
        for (UUID uuid : linkedMobs) {
            Entity mob = Bukkit.getEntity(uuid);
            if (mob != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> killLinkedMob(mob),
                    (long) (i++ * random.nextDouble() * 30));
            }
        }
    }

    private void killLinkedMob(Entity mob) {
        Location location = mob.getLocation();
        for (int i = 0; i < 20; i++) {
            double xi = random.nextDouble() - .5;
            double yi = random.nextDouble() * 2;
            double zi = random.nextDouble() - .5;
            location.getWorld().spawnParticle(Particle.LARGE_SMOKE, location, 1, xi, yi, zi, 1);
        }
        location.getWorld()
            .playSound(location, Sound.ITEM_TOTEM_USE, SoundCategory.HOSTILE, 10, 1.3f);
        mob.remove();
    }
}
