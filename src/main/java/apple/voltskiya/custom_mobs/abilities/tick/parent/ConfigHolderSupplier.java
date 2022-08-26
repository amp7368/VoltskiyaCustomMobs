package apple.voltskiya.custom_mobs.abilities.tick.parent;

import apple.configs.data.config.AppleConfig.Builder;
import apple.lib.pmc.PluginModule;
import apple.utilities.util.FileFormatting;

public interface ConfigHolderSupplier<Holder> {

    default Holder getConfigHolder() {
        Builder<? extends Holder> builder = getModule().configJson(getConfigClass(),
            getParentName(), getFile());
        builder.build()[0].register();
        return builder.getConfig().getInstance();
    }

    default String[] getFile() {
        return new String[]{getParentName(), FileFormatting.extensionYml(getParentName())};
    }

    Class<? extends Holder> getConfigClass();

    PluginModule getModule();

    String getParentName();
}
