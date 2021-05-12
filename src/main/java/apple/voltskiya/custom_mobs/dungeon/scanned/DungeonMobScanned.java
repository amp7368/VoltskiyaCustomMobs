package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobInfo;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.dungeon.scanner.JsonKeys;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DungeonMobScanned {
    private DungeonMobInfo mob;
    private Entity entity = null;
    private DungeonMobConfig config = null;

    public DungeonMobScanned(@NotNull DungeonScanner scanner, Entity entity) {
        this.mob = new DungeonMobInfo(entity);
        this.entity = entity;
        this.config = scanner.getMobConfig(entity);
    }

    public DungeonMobScanned(Dungeon dungeon, JsonObject fromJson) throws CommandSyntaxException, IllegalStateException {
        this.mob = new DungeonMobInfo(fromJson.get(JsonKeys.DUNGEON_MOB_PRIMARY).getAsJsonObject());
        JsonElement json = fromJson.get(JsonKeys.DUNGEON_MOB_CONFIG);
        if (json != null && !json.isJsonNull()) {
            DungeonScanner dungeonScanner = dungeon.getScanner();
            if (dungeonScanner == null) {
                throw new IllegalStateException("There is no scanner to provide config info");
            }
            this.config = dungeonScanner.getMobConfig(json.getAsString());
            if (this.config == null) {
                throw new IllegalStateException("There is no mob_config for " + json.getAsString());
            }
        }
    }


    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(JsonKeys.DUNGEON_MOB_PRIMARY, mob.toJson());
        json.add(JsonKeys.DUNGEON_MOB_CONFIG, config == null ? JsonNull.INSTANCE : new JsonPrimitive(config.getName()));
        return json;
    }

    public void rotate(int degrees) {
        if (entity != null) {
            final Location location = entity.getLocation();
            location.getDirection().rotateAroundY(Math.toRadians(degrees));
            entity.teleport(location);
            mob = new DungeonMobInfo(entity);
        }
    }

    public void pitchAdd(int degrees) {
        final Location location = entity.getLocation();
        location.setPitch(location.getPitch() + degrees);
        entity.teleport(location);
    }

    public ItemStack toItem() {
        if (config == null) return mob.toItem();
        return config.toItem();
    }

    public ItemStack toItem(Player player) {
        double distance = distance(player);
        if (config == null) return mob.toItem(distance);
        return config.toItem(distance);
    }

    public double distance(Player player) {
        final Location l = getLocation();
        return l == null ? Double.MAX_VALUE : (VectorUtils.magnitude(player.getLocation().toVector().subtract(l.toVector())));
    }

    public Location getLocation() {
        return mob.getLocation();
    }

    public String getName() {
        return mob.getName();
    }

    public DungeonMobInfo getMobSpawn() {
        return config == null ? this.mob : config.getSpawnedMob();
    }

    public Vector getOffset(DungeonScanned scanned) {
        if (scanned == null) throw new IllegalStateException("There is no scanned dungeon somehow");
        Location center = scanned.getCenter();
        if (center == null) throw new IllegalStateException("There is no center to the scanned dungeon");
        final Location mobLocation = mob.getLocation();
        if (mobLocation == null) throw new IllegalStateException("There is no location in the scanned mob");
        return mobLocation.toVector().subtract(center.toVector());
    }
}
