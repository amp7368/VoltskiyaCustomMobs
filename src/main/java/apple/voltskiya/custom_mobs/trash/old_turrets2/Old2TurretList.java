package apple.voltskiya.custom_mobs.trash.old_turrets2;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMob;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Old2TurretList {
    private static final HashMap<String, Old2TurretMob> turretMobs = new HashMap<>();
    private static Gson gson;
    private static File turretMobFolder;
    private static final ArrayList<String> turretMobToRemove = new ArrayList<>();

    private synchronized static void save(Old2TurretMobSaveable mob) {
        File file = new File(turretMobFolder, mob.getUUID() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(mob, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void initialize() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Old2TurretMobSaveable.class, new TurretAdapter());
        gson = gsonBuilder.create();
        turretMobFolder = new File(Old2TurretPlugin.get().getDataFolder(), "turretMob.json");
        try {
            turretMobFolder.mkdirs();
            File[] files = turretMobFolder.listFiles();
            if (files != null) {
                for (File turretMobFile : files) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(turretMobFile))) {
                        Old2TurretMob turretMob = gson.fromJson(reader, Old2TurretMobSaveable.class).build();
                        if (turretMob.verifyAlive())
                            turretMobs.put(turretMob.getUUID(), turretMob);
                        else {
                            reader.close();
                            turretMobFile.delete();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), new TurretSaveDaemon(), TurretSaveDaemon.TICKS_TO_SAVE);
        HighFrequencyTick.get().add(Old2TurretList::tick);
    }

    public static void registerOrUpdate(Old2TurretMob turretMob) {
        synchronized (turretMobs) {
            turretMobs.put(turretMob.getUUID(), turretMob);
            synchronized (TurretSaveDaemon.sync) {
                TurretSaveDaemon.turretMobsToSave.add(turretMob);
            }
        }
    }

    public static boolean interact(Player player, Entity entity) {
        synchronized (turretMobs) {
            for (Old2TurretMob turretMob : turretMobs.values()) {
                if (turretMob.interact(player, entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void tick() {
        synchronized (turretMobs) {
            for (Old2TurretMob turretMob : turretMobs.values()) {
                turretMob.run();
            }
        }
    }

    public static void remove(Old2TurretMob turretMob) {
        synchronized (turretMobToRemove) {
            turretMobToRemove.add(turretMob.getUUID());
        }
        File file = new File(turretMobFolder, turretMob.getUUID() + ".json");
        file.delete();
    }

    public static void damage(double damage, Entity entity, EntityDamageByEntityEvent event) {
        for (Old2TurretMob turretMob : turretMobs.values()) {
            if (turretMob.isMe(entity)) {
                if (!entity.getScoreboardTags().contains(Old2TurretMob.TURRET_TAG))
                    turretMob.damage(damage);
                event.setCancelled(true);
                if (event.getDamager() instanceof AbstractArrow arrow) {
                    arrow.remove();
                }
                return;
            }
        }
    }

    public Collection<Old2TurretMob> getAll() {
        return turretMobs.values();
    }

    private static class TurretAdapter implements JsonSerializer<Old2TurretMobSaveable>, JsonDeserializer<Old2TurretMobSaveable> {
        @Override
        public JsonElement serialize(Old2TurretMobSaveable turretMob, Type src, JsonSerializationContext context) {
            JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
            result.add(Old2TurretMob.getTypeIdFieldName(), new JsonPrimitive(turretMob.getTypeId()));
            return result;
        }

        @Override
        public Old2TurretMobSaveable deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String typeId = jsonObject.get(Old2TurretMob.getTypeIdFieldName()).getAsString();
            Old2TurretType turretMobType = Old2TurretType.getType(typeId);
            if (turretMobType == null) {
                throw new JsonParseException("Unknown element type: " + type + ":" + typeId);
            }
            Class<? extends Old2TurretMobSaveable> classOfTurretMob = turretMobType.type();
            return context.deserialize(json, classOfTurretMob);
        }
    }

    private static class TurretSaveDaemon implements Runnable {
        private static final long TICKS_TO_SAVE = 400;
        private static final Set<Old2TurretMob> turretMobsToSave = new HashSet<>();
        private static final Object sync = new Object();

        @Override
        public void run() {
            List<Old2TurretMob> turretMobsTemp;
            synchronized (sync) {
                turretMobsTemp = new ArrayList<>(turretMobsToSave);
                turretMobsToSave.clear();
            }
            synchronized (turretMobs) {
                // this should only ever be held for very brief periods of time with
                // no other synchronized blocks
                synchronized (turretMobToRemove) {
                    for (String removeMe : turretMobToRemove) {
                        turretMobs.remove(removeMe);
                    }
                }
            }
            for (Old2TurretMob turretMob : turretMobsTemp) {
                save(turretMob.toSaveable());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, TICKS_TO_SAVE);
        }
    }
}
