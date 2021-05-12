package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.patrols.Patrol;
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
import org.jetbrains.annotations.NotNull;

public class DungeonMobScanned {
    private final DungeonMobInfo mob;
    private DungeonMobConfig config = null;
    private Patrol patrol = null;

    public DungeonMobScanned(@NotNull DungeonScanner scanner, Entity entity) {
        this.mob = new DungeonMobInfo(entity);
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
        mob.rotate(degrees);
    }

    public void pitchAdd(int degrees) {
        mob.pitchAdd(degrees);
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

    public DungeonMobInfo getMobPrimary() {
        return mob;
    }

    public Patrol getPatrol(Dungeon dungeon) {
        return patrol == null ? patrol = new Patrol(dungeon, this) : patrol;
    }

    public String getUUID() {
        return mob.getUUID();
    }
}
