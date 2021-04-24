package apple.voltskiya.custom_mobs.custom_model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

class CustomModelGuiList {
    private static final Map<UUID, CustomModelGui> modelGuis = new HashMap<>();

    @Nullable
    public static synchronized CustomModelGui get(UUID uuid) {
        return modelGuis.get(uuid);
    }

    public static synchronized UUID put(CustomModelGui gui) {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (modelGuis.containsKey(uuid));
        modelGuis.put(uuid, gui);
        return uuid;
    }
}
