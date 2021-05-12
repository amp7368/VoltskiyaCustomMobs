package apple.voltskiya.custom_mobs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Location;

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
        return null;
    }
}
