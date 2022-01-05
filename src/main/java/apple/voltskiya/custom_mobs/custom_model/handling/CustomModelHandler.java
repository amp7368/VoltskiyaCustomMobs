package apple.voltskiya.custom_mobs.custom_model.handling;

import apple.voltskiya.custom_mobs.custom_model.CustomModelData;
import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class CustomModelHandler<Model extends CustomModelConfig<ModelEntityConfig>, ModelEntityConfig extends CustomModelEntityConfig> {
    private final Map<ModelName, Model> allModels = new HashMap<>();

    protected void registerAllModels() {
        for (ModelName name : values()) {
            Model model = registerModel(name.getFile());
            if (model != null) {
                allModels.put(name, model);
            }
        }
    }

    protected abstract ModelName[] values();

    public Model getModel(ModelName name) {
        return this.allModels.get(name);
    }

    @Nullable
    protected Model registerModel(File file) {
        @Nullable CustomModelData modelData = CustomModelPlugin.get().loadSchematic(file);
        if (modelData == null) {
            logNoModelError(file);
            return null;
        }
        Model model = createEmptyModel();
        for (CustomModelDataEntity part : modelData.entities) {
            model.addPiece(createEntity(part));
        }
        if (model.validate()) {
            return model;
        } else {
            logModelError(file);
            return null;
        }
    }

    private void logNoModelError(File file) {
        getModule().log(Level.WARNING, file.getPath() + " has no schematic");
    }

    protected void logModelError(File file) {
        getModule().log(Level.WARNING, file.getPath() + " has an invalid schematic");
    }

    protected abstract PluginManagedModule getModule();

    protected abstract ModelEntityConfig createEntity(CustomModelDataEntity part);

    protected abstract Model createEmptyModel();
}
