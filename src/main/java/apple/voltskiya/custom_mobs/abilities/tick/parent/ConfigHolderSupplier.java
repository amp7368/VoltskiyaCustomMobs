package apple.voltskiya.custom_mobs.abilities.tick.parent;

import apple.lib.configs.data.config.AppleConfig.Builder;
import apple.lib.pmc.AppleModule;

public interface ConfigHolderSupplier<Holder> {

    default Holder getConfigHolder() {
        Builder<? extends Holder> builder = getModule().configJson(getConfigClass(),
            getParentName());
        builder.build()[0].register();
        return builder.getConfig().getInstance();
    }

    Class<? extends Holder> getConfigClass();

    AppleModule getModule();

    String getParentName();
}
