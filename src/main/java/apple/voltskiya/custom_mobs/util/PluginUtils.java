package apple.voltskiya.custom_mobs.util;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class PluginUtils extends PluginManagedModule {
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
