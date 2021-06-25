package apple.voltskiya.custom_mobs.util;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class PluginUtils extends VoltskiyaModule {
    private static PluginUtils instance;

    public static PluginUtils get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new UpdatedPlayerList();
    }

    @Override
    public String getName() {
        return "utilities";
    }
}
