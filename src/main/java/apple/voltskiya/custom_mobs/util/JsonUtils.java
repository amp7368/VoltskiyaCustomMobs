package apple.voltskiya.custom_mobs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

public class JsonUtils {
    public static JsonElement locationToJson(Location location) {
        final JsonObject json = new JsonObject();
        json.add("world", new JsonPrimitive(location.getWorld().getUID().toString()));
        json.add("x", new JsonPrimitive(location.getX()));
        json.add("y", new JsonPrimitive(location.getY()));
        json.add("z", new JsonPrimitive(location.getZ()));
        json.add("xF", new JsonPrimitive(location.getDirection().getX()));
        json.add("yF", new JsonPrimitive(location.getDirection().getY()));
        json.add("zF", new JsonPrimitive(location.getDirection().getZ()));
        return json;
    }

    public static Location locationFromJson(JsonElement location) {
        JsonObject json = location.getAsJsonObject();
        return new Location(
                Bukkit.getWorld(UUID.fromString(json.get("world").getAsString())),
                json.get("x").getAsDouble(),
                json.get("y").getAsDouble(),
                json.get("z").getAsDouble()
        ).setDirection(
                new Vector(json.get("xF").getAsDouble(),
                        json.get("yF").getAsDouble(),
                        json.get("zF").getAsDouble()
                ));
    }
}
