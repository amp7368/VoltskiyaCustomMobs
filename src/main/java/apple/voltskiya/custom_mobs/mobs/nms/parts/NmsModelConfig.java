package apple.voltskiya.custom_mobs.mobs.nms.parts;

import apple.utilities.util.FileFormatting;
import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class NmsModelConfig {
    private static final Map<ModelConfigName, NmsModelConfig> ALL_PARTS = new HashMap<>();
    public static final File MODEL_FOLDER = PluginNmsMobs.get().getModelDataFolder();
    private final NmsModelEntityConfig mainConfig;
    private final List<NmsModelEntityConfig> partsConfig;

    public NmsModelConfig(NmsModelEntityConfig main, ArrayList<NmsModelEntityConfig> partList) {
        this.mainConfig = main;
        this.partsConfig = partList;
    }

    // initialize the parts
    public static void initialize() {
        for (ModelConfigName name : ModelConfigName.values()) {
            NmsModelConfig model = registerModel(name.getFile());
            if (model != null) {
                ALL_PARTS.put(name, model);
            }
        }
    }

    @Nullable
    private static NmsModelConfig registerModel(File file) {
        @Nullable CustomModel model = CustomModelPlugin.get().loadSchematic(file);
        if (model == null) {
            PluginNmsMobs.get().log(Level.WARNING, file.getPath() + " has no schematic");
            return null;
        }
        final ArrayList<NmsModelEntityConfig> partList = new ArrayList<>();
        NmsModelEntityConfig main = null;
        for (CustomModel.CustomEntity part : model.entities) {
            final NmsModelEntityConfig piece = new NmsModelEntityConfig(part);
            if (piece.isMain()) {
                main = piece;
            } else partList.add(piece);
        }
        if (main == null) {
            PluginNmsMobs.get().log(Level.WARNING, file.getPath() + " has an invalid schematic");
            return null;
        } else {
            partList.trimToSize();
            return new NmsModelConfig(main, partList);
        }
    }

    public static NmsModelConfig parts(ModelConfigName name) {
        return ALL_PARTS.get(name);
    }

    public NmsModelEntityConfig mainPart() {
        return this.mainConfig;
    }

    public List<NmsModelEntityConfig> others() {
        return this.partsConfig;
    }

    // list the parts
    public enum ModelConfigName {
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
            return FileFormatting.fileWithChildren(NmsModelConfig.MODEL_FOLDER, folder);
        }

        public String getTag() {
            return getName();
        }
    }
}
