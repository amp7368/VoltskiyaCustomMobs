package apple.voltskiya.custom_mobs.turret.manage.model.config;

import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelHandler;
import apple.voltskiya.custom_mobs.custom_model.handling.ModelName;
import apple.voltskiya.custom_mobs.turret.PluginTurret;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class TurretModelHandlerConfig extends CustomModelHandler<TurretModelConfig, TurretModelEntityConfig> {
    private static TurretModelHandlerConfig instance;

    public TurretModelHandlerConfig() {
        instance = this;
        registerAllModels();
    }

    public static TurretModelHandlerConfig get() {
        return instance;
    }

    @Override
    protected ModelName[] values() {
        return TurretModelNames.values();
    }

    @Override
    protected PluginManagedModule getModule() {
        return PluginTurret.get();
    }

    @Override
    protected TurretModelEntityConfig createEntity(CustomModelDataEntity part) {
        return new TurretModelEntityConfig(part);
    }

    @Override
    protected TurretModelConfig createEmptyModel() {
        return new TurretModelConfig();
    }

}
