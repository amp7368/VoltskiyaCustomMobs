package apple.voltskiya.custom_mobs.util;


import apple.lib.pmc.AppleModule;

public class PluginUtils extends AppleModule {

    private static PluginUtils instance;

    public static PluginUtils get() {
        return instance;
    }

    public PluginUtils() {
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
