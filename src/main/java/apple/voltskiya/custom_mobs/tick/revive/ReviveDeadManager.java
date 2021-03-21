package apple.voltskiya.custom_mobs.tick.revive;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.tick.DeathEater;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ReviveDeadManager extends DeathEater {

    private static final double PARTICLE_RADIUS = .5;
    private static int TIME_TO_RISE;
    public long MAX_DEAD_TIME;
    private static ReviveDeadManager instance;
    private final ArrayList<RecordedMob> dead = new ArrayList<>();
    private final Random random = new Random();

    public ReviveDeadManager() throws IOException {
        instance = this;
        MAX_DEAD_TIME = (int) getValueOrInit(getName(), YmlSettings.MAX_DEAD_TIME.getPath(), "dead") * 1000 / 20;
        TIME_TO_RISE = (int) getValueOrInit(getName(), YmlSettings.TIME_TO_RISE.getPath(), "dead");
    }


    public static ReviveDeadManager get() {
        return instance;
    }

    @Override
    public synchronized void eatEvent(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains("was_revived_2")) return;
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

    @Nullable
    public synchronized RecordedMob reviveStart(Location location) {
        Iterator<RecordedMob> iterator = dead.iterator();
        while (iterator.hasNext()) {
            RecordedMob mob = iterator.next();
            if (mob.isNearby(location)) {
                iterator.remove();
                return mob;
            }
        }
        return null;
    }

    public synchronized void reviveStart(RecordedMob reviveMe, CraftMob reviver,Reviver reviverObject) {
        reviver.setAI(false);

        final Location reviverLocation = reviver.getLocation();
        final Location reviveMeLocation = reviveMe.getEntity().getLocation();
        double x = reviveMeLocation.getX() - reviverLocation.getX();
        double z = reviveMeLocation.getZ() - reviverLocation.getZ();
        double magnitude = x * x + z * z;

        Location newLoc = reviverLocation.setDirection(new Vector(x / magnitude, -.5, z / magnitude));
        reviver.teleport(newLoc);

        final World world = reviverLocation.getWorld();
        world.playSound(reviveMe.location, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 35, .7f);
        final int time = ReviverManagerTicker.get().REVIVE_RITUAL_TIME + TIME_TO_RISE;
        for (int timeI = 0; timeI < time; timeI += 3) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                double xLoc = reviverLocation.getX();
                double yLoc = reviverLocation.getY();
                double zLoc = reviverLocation.getZ();
                for (int i = 0; i < 15; i++) {
                    double xi = random.nextDouble() - .5;
                    double yi = random.nextDouble() * 2;
                    double zi = random.nextDouble() - .5;
                    world.spawnParticle(Particle.REDSTONE, xLoc + xi, yLoc + yi, zLoc + zi, 1, new Particle.DustOptions(Color.RED, 1f));
                }
            }, timeI);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            if (!reviver.isDead()) {
                reviver.setAI(true);
                reviver.removePotionEffect(PotionEffectType.SPEED);
                reviveProcess(reviveMe, reviver,reviverObject);
            }
        }, time);
    }

    public synchronized void reviveProcess(RecordedMob reviveMe, CraftMob reviver, Reviver reviverObject) {
        final net.minecraft.server.v1_16_R3.Entity original = ((CraftEntity) reviveMe.getEntity()).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        original.save(nbt);
        reviveMe.location.getWorld().spawnEntity(reviveMe.location, reviveMe.getEntity().getType(), CreatureSpawnEvent.SpawnReason.CUSTOM,
                newMob -> {
                    reviverObject.addMob(newMob);
                    final net.minecraft.server.v1_16_R3.Entity newMobHandle = ((CraftEntity) newMob).getHandle();
                    nbt.set("UUID", NBTTagString.a(newMobHandle.getUniqueIDString()));
                    nbt.set("DeathTime", NBTTagInt.a(0));
                    newMobHandle.load(nbt);
                    ((LivingEntity) newMob).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(reviveMe.getHealth());
                    ((LivingEntity) newMob).setHealth(reviveMe.getHealth());
                    if (newMob.getScoreboardTags().contains("was_revived_1")) {
                        newMob.addScoreboardTag("was_revived_2");
                    } else {
                        newMob.addScoreboardTag("was_revived_1");
                    }
                    ((LivingEntity) newMob).setAI(false);
                    newMob.setInvulnerable(true);
                    reviveMe.location.add(0, -3, 0);
                    reviveMe.location.setPitch(-55);
                    newMob.teleport(reviveMe.location);
                    double interval = 3d / TIME_TO_RISE;
                    for (int time = 0; time < TIME_TO_RISE; time++) {
                        if (time % 3 == 0)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                                reviver.getLocation().getWorld().playSound(reviver.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 6, 0.75f);
                            }, time);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                            Location newLocation = newMob.getLocation();
                            particles(newLocation);
                            newLocation.add(0, interval, 0);
                            newMob.teleport(newLocation);
                        }, time);
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                        ((LivingEntity) newMob).setAI(true);
                        newMob.setInvulnerable(false);
                        ((Mob) newMob).setLootTable(LootTables.EMPTY.getLootTable());
                    }, TIME_TO_RISE);
                }
        );

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
            location.getWorld().spawnParticle(Particle.SPELL_WITCH, xi + x, yi + y, zi + z, 1);
        }
    }

    public static class RecordedMob {

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
            return location.getWorld().equals(loc.getWorld()) && location.distance(loc) <= ReviverManagerTicker.get().REVIVE_DISTANCE;
        }

        public LivingEntity getEntity() {
            return entity;
        }
    }

    private enum YmlSettings {
        MAX_DEAD_TIME("max_dead_time", 600),
        TIME_TO_RISE("time_to_rise", 50);

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
