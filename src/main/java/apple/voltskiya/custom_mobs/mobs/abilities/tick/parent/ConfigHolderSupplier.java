package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.utilities.util.FileFormatting;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;

public interface ConfigHolderSupplier<Holder> {
    default Holder getConfigHolder() {
        return getModule().registerConfigYml(getConfigClass(), getParentName(), getFile());
    }

    default String[] getFile() {
        return new String[]{getParentName(), FileFormatting.extensionYml(getParentName())};
    }

    Class<? extends Holder> getConfigClass();

    PluginManagedModuleConfig getModule();

    String getParentName();
}
