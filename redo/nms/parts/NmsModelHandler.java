package apple.voltskiya.custom_mobs.mobs.nms.parts;

import apple.utilities.util.FileFormatting;
import apple.voltskiya.custom_mobs.trash.dungeon.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.trash.dungeon.custom_model.handling.CustomModelHandler;
import apple.voltskiya.custom_mobs.trash.dungeon.custom_model.handling.ModelName;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import org.jetbrains.annotations.NotNull;
import apple.mc.utilities.PluginModule;

import java.io.File;


public class NmsModelHandler extends CustomModelHandler<NmsModel, NmsModelEntityConfig> {
    public static final File MODEL_FOLDER = PluginNmsMobs.get().getModelDataFolder();
    public static NmsModelHandler instance;

    // initialize the parts
    public NmsModelHandler() {
        instance = this;
        registerAllModels();
    }


    public static NmsModel parts(ModelConfigName name) {
        return instance.getModel(name);
    }

    @Override
    protected ModelName[] values() {
        return ModelConfigName.values();
    }

    @Override
    protected PluginModule getModule() {
        return PluginNmsMobs.get();
    }

    @Override
    protected NmsModelEntityConfig createEntity(CustomModelDataEntity part) {
        return new NmsModelEntityConfig(part);
    }

    @Override
    protected NmsModel createEmptyModel() {
        return new NmsModel();
    }


    // list the parts
    public enum ModelConfigName implements ModelName {
        WARPED_GREMLIN("warped_gremlin"),
        ALEDAR_CART("aledar_cart"),
        CART("rideable_cart"),
        EYE_PLANT("eye_plant"),
        MISC_MODEL("misc_model"),
        PARASITE("nether_parasite"),
        APC33_MOB("apc33_mob", "apc33"),
        APC33_TREADS("apc33_treads", "apc33"),
        APC33_GUN("apc33_gun", "apc33"),
        APC33_CANNON("apc33_cannon", "apc33");

        private final String fileName;
        private final String[] folder;

        ModelConfigName(String fileName, String... folder) {
            this.fileName = fileName;
            this.folder = folder;
        }

        public @NotNull String getName() {
            return this.fileName;
        }

        public File getFile() {
            return new File(getFolder(), FileFormatting.extensionYml(fileName));
        }

        public File getFolder() {
            return FileFormatting.fileWithChildren(NmsModelHandler.MODEL_FOLDER, folder);
        }

        public String getTag() {
            return getName();
        }
    }
}
