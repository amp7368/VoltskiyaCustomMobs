package apple.voltskiya.custom_mobs.trash.revive;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.DeathEater;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class OldReviveDeadManager extends ConfigManager implements DeathEater {

    private static final double PARTICLE_RADIUS = .5;
    public static final int DIED_COOLDOWN = 1000 * 5;
    private static int TIME_TO_RISE;
    public static long MAX_DEAD_TIME;
    private static OldReviveDeadManager instance;
    private final ArrayList<OldRecordedMob> dead = new ArrayList<>();
    private final Random random = new Random();

    public OldReviveDeadManager() throws IOException {
        instance = this;
        MAX_DEAD_TIME = (int) getValueOrInit(getName(), YmlSettings.MAX_DEAD_TIME.getPath(), "dead") * 1000 / 20;
        TIME_TO_RISE = (int) getValueOrInit(getName(), YmlSettings.TIME_TO_RISE.getPath(), "dead");
    }


    public static OldReviveDeadManager get() {
        return instance;
    }

    @Override
    public synchronized void eatEvent(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains("was_revived_2")) return;
        dead.removeIf(OldRecordedMob::shouldRemove);
        dead.add(new OldRecordedMob(event.getEntity()));
        dead.trimToSize();
        // don't add it to the db
    }

    @Override
    public String getName() {
        return "revive";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(getName(), setting.getPath(), setting.value, "dead");
        }
    }

    @Override
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
    }

    @Nullable
    public synchronized OldRecordedMob reviveStart(Location location, MobReviver reviverObject) {
        for (OldRecordedMob mob : dead) {
            if (mob.isNearby(location, reviverObject) && mob.isReviveableNow()) {
                mob.setReviving();
                return mob;
            }
        }
        return null;
    }

    public synchronized void reviveStart(OldRecordedMob reviveMe, CraftMob reviver, MobReviver reviverObject) {

    }

    public synchronized void reviveProcess(OldRecordedMob reviveMe, CraftMob reviver, MobReviver reviverObject) {
        final net.minecraft.world.entity.Entity original = ((CraftEntity) reviveMe.getEntity()).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        original.save(nbt);
        reviveMe.location.getWorld().spawnEntity(reviveMe.location, reviveMe.getEntity().getType(), CreatureSpawnEvent.SpawnReason.CUSTOM,
                newMob -> {
                    reviverObject.addLinkedMob(newMob);
                    final net.minecraft.world.entity.Entity newMobHandle = ((CraftEntity) newMob).getHandle();
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
                        reviveMe.remove();
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

    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
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
