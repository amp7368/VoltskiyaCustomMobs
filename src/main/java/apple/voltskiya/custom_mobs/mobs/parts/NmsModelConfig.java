package apple.voltskiya.custom_mobs.mobs.parts;

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

    static {
        if (!MODEL_FOLDER.exists()) MODEL_FOLDER.mkdirs();
    }

    public NmsModelConfig(NmsModelEntityConfig main, ArrayList<NmsModelEntityConfig> partList) {
        this.mainConfig = main;
        this.partsConfig = partList;
    }

    // initialize the parts
    public static void initialize() {
        for (ModelConfigName name : ModelConfigName.values()) {
            NmsModelConfig model = registerModel(MODEL_FOLDER, name.getFile());
            if (model != null) {
                ALL_PARTS.put(name, model);
            }
        }
    }

    @Nullable
    private static NmsModelConfig registerModel(File folder, String name) {
        @Nullable CustomModel model = CustomModelPlugin.get().loadSchematic(new File(folder, name + ".yml"));
        if (model == null) {
            PluginNmsMobs.get().log(Level.WARNING, name + " has no schematic");
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
            PluginNmsMobs.get().log(Level.WARNING, name + " has an invalid schematic");
            return null;
        } else {
            partList.trimToSize();
            return new NmsModelConfig(main, partList);
        }
    }

    public static NmsModelConfig parts(ModelConfigName name) {
        return ALL_PARTS.get(name);
    }

    @Nullable
    public static NmsModelConfig parts(String name) {
        return registerModel(MODEL_FOLDER, name);
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
        PARASITE("nether_parasite");

        private final String fileName;

        ModelConfigName(String fileName) {
            this.fileName = fileName;
        }

        public @NotNull String getFile() {
            return this.fileName;
        }
    }
}
