package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.turrets.mobs.TurretMob;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TurretList {
    private static final HashMap<String, TurretMob> turretMobs = new HashMap<>();
    private static Gson gson;
    private static File turretMobFolder;

    private static void save(TurretMobSaveable mob) {
        File file = new File(turretMobFolder, mob.getUUID() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(mob, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TurretMobSaveable.class, new TurretAdapter());
        gson = gsonBuilder.create();
        turretMobFolder = new File(TurretPlugin.get().getDataFolder(), "turretMob.json");
        try {
            turretMobFolder.mkdirs();
            File[] files = turretMobFolder.listFiles();
            if (files != null) {
                for (File turretMobFile : files) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(turretMobFile))) {
                        TurretMob turretMob = gson.fromJson(reader, TurretMobSaveable.class).build();
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
        HighFrequencyTick.get().add(TurretList::tick);
    }

    public static void registerOrUpdate(TurretMob turretMob) {
        turretMobs.put(turretMob.getUUID(), turretMob);
        synchronized (TurretSaveDaemon.sync) {
            TurretSaveDaemon.turretMobsToSave.add(turretMob);
        }
    }

    public static boolean interact(Player player, Entity entity) {
        for (TurretMob turretMob : turretMobs.values()) {
            if (turretMob.interact(player, entity)) {
                return true;
            }
        }
        return false;
    }

    private static void tick() {
        for (TurretMob turretMob : turretMobs.values()) {
            turretMob.run();
        }
    }

    public static void remove(TurretMob turretMob) {
        turretMobs.remove(turretMob.getUUID());
        File file = new File(turretMobFolder, turretMob.getUUID() + ".json");
        file.delete();
    }

    public static void damage(double damage, Entity entity) {
        for (TurretMob turretMob : turretMobs.values()) {
            if (turretMob.isMe(entity)) {
                turretMob.damage(damage);
                return;
            }
        }
    }

    public Collection<TurretMob> getAll() {
        return turretMobs.values();
    }

    private static class TurretAdapter implements JsonSerializer<TurretMobSaveable>, JsonDeserializer<TurretMobSaveable> {
        @Override
        public JsonElement serialize(TurretMobSaveable turretMob, Type src, JsonSerializationContext context) {
            JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
            result.add(TurretMob.getTypeIdFieldName(), new JsonPrimitive(turretMob.getTypeId()));
            return result;
        }

        @Override
        public TurretMobSaveable deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String typeId = jsonObject.get(TurretMob.getTypeIdFieldName()).getAsString();
            TurretType turretMobType = TurretType.getType(typeId);
            if (turretMobType == null) {
                throw new JsonParseException("Unknown element type: " + type + ":" + typeId);
            }
            Class<? extends TurretMobSaveable> classOfTurretMob = turretMobType.type();
            return context.deserialize(json, classOfTurretMob);
        }
    }

    private static class TurretSaveDaemon implements Runnable {
        private static final long TICKS_TO_SAVE = 400;
        private static final Set<TurretMob> turretMobsToSave = new HashSet<>();
        private static final Object sync = new Object();

        @Override
        public void run() {
            List<TurretMob> turretMobsTemp;
            synchronized (sync) {
                turretMobsTemp = new ArrayList<>(turretMobsToSave);
                turretMobsToSave.clear();
            }
            for (TurretMob turretMob : turretMobsTemp) {
                save(turretMob.toSaveable());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, TICKS_TO_SAVE);
        }
    }
}
