package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobInfo;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
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
import org.jetbrains.annotations.Nullable;

public class DungeonMobScanned {
    private DungeonMobInfo mob;
    private Entity entity = null;
    private DungeonMobConfig config = null;

    public DungeonMobScanned(Entity entity) {
        this.mob = new DungeonMobInfo(entity);
        this.entity = entity;
    }

    public DungeonMobScanned(DungeonScanner dungeonScanner, JsonObject fromJson) throws CommandSyntaxException {
        this.mob = new DungeonMobInfo(fromJson.get(DungeonScanner.JsonKeys.DUNGEON_MOB_PRIMARY).getAsJsonObject());
        JsonElement json = fromJson.get(DungeonScanner.JsonKeys.DUNGEON_MOB_CONFIG);
        if (json != null && !json.isJsonNull()) this.config = dungeonScanner.getMobConfig(json.getAsString());
    }

    public void setConfig(DungeonMobConfig mobConfig) {
        this.config = mobConfig;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOB_PRIMARY, mob.toJson());
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOB_CONFIG, config == null ? JsonNull.INSTANCE : new JsonPrimitive(config.getName()));
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
        final Vector l = getLocation();
        return l == null ? Double.MAX_VALUE : (VectorUtils.magnitude(player.getLocation().toVector().subtract(l)));
    }

    @Nullable
    public Vector getLocation() {
        return mob.getLocation();
    }

    public String getName() {
        return mob.getName();
    }

    public void spawn(DungeonLocation realDungeon, Location center) {
        DungeonMobInfo mob = config == null ? this.mob : config.getSpawnedMob();
        realDungeon.spawn(mob, mob.getLocation().subtract(center.toVector()));
    }
}
