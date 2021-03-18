package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.heartbeat.tick.DeathEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrikeManagerTicker;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ReviveDeadManager extends DeathEater {

    private static final double PARTICLE_RADIUS = .7;
    public long MAX_DEAD_TIME;
    private static ReviveDeadManager instance;
    private final ArrayList<RecordedMob> dead = new ArrayList<>();
    private final Random random = new Random();

    public ReviveDeadManager() throws IOException {
        instance = this;
        MAX_DEAD_TIME = (int) getValueOrInit(getName(), YmlSettings.MAX_DEAD_TIME.getPath(), "dead") * 1000 / 20;
    }


    public static ReviveDeadManager get() {
        return instance;
    }

    @Override
    public synchronized void eatEvent(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains("was_revived")) return;
        final long now = System.currentTimeMillis();
        dead.removeIf(uuid -> now - uuid.diedTime > MAX_DEAD_TIME);
        dead.add(new RecordedMob(event.getEntity()));
        dead.trimToSize();
        // don't add it to the db
    }

    @Override
    public String getName() {
        return "revive";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(getName(), setting.getPath(), setting.value, "dead");
        }
    }

    public synchronized void revive(Location location) {
        Iterator<RecordedMob> iterator = dead.iterator();
        while (iterator.hasNext()) {
            RecordedMob mob = iterator.next();
            if (mob.isNearby(location)) {
                revive(mob, location);
                iterator.remove();
            }
        }
    }

    private synchronized void revive(RecordedMob mob, Location location) {
        final net.minecraft.server.v1_16_R3.Entity original = ((CraftEntity) mob.getEntity()).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        original.save(nbt);
        mob.location.getWorld().spawnEntity(mob.location, mob.getEntity().getType(), CreatureSpawnEvent.SpawnReason.CUSTOM,
                newMob -> {
                    final net.minecraft.server.v1_16_R3.Entity newMobHandle = ((CraftEntity) newMob).getHandle();
                    nbt.set("UUID", NBTTagString.a(newMobHandle.getUniqueIDString()));
                    nbt.set("DeathTime", NBTTagInt.a(0));
                    newMobHandle.load(nbt);
                    ((LivingEntity) newMob).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mob.getHealth());
                    ((LivingEntity) newMob).setHealth(mob.getHealth());
                    newMob.addScoreboardTag("was_revived");
                    newMob.teleport(mob.location);
                }
        );
        double xi = mob.location.getX();
        double yi = mob.location.getY();
        double zi = mob.location.getZ();
        for (int i = 0; i < 70; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * PARTICLE_RADIUS;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.nextDouble() * 2;
            mob.location.getWorld().spawnParticle(Particle.SPELL_WITCH, xi + x, yi + y, zi + z, 1);
        }
        mob.location.getWorld().playSound(mob.location, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 30, .7f);

    }

    private static class RecordedMob {

        private final double health;
        private final Location location;
        private final LivingEntity entity;
        public long diedTime = System.currentTimeMillis();

        public RecordedMob(LivingEntity entity) {
            @Nullable AttributeInstance hp = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            this.health = hp == null ? 0 : hp.getBaseValue();
            this.location = entity.getLocation();
            this.entity = entity;
        }

        public double getHealth() {
            return health;
        }

        public boolean isNearby(Location loc) {
            return location.distance(loc) <= ReviverManagerTicker.get().REVIVE_DISTANCE;
        }

        public LivingEntity getEntity() {
            return entity;
        }
    }

    private enum YmlSettings {
        MAX_DEAD_TIME("max_dead_time", 400);

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
