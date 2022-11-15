package apple.voltskiya.custom_mobs.util;


import com.voltskiya.lib.AbstractModule;

public class PluginUtils extends AbstractModule {

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
