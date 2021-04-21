package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
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
    private final NmsModelEntityConfig mainConfig;
    private final List<NmsModelEntityConfig> partsConfig;

    public NmsModelConfig(NmsModelEntityConfig main, ArrayList<NmsModelEntityConfig> partList) {
        this.mainConfig = main;
        this.partsConfig = partList;
    }

    // initialize the parts
    public static void initialize() {
        File folder = new File(NmsMobsPlugin.get().getDataFolder(), "models");
        if (!folder.exists()) folder.mkdirs();
        for (ModelConfigName name : ModelConfigName.values()) {
            @Nullable CustomModel model = CustomModelPlugin.get().loadSchematic(new File(folder, name.getFile() + ".yml"));
            if (model == null) {
                NmsMobsPlugin.get().log(Level.WARNING, name.getFile() + " has no schematic");
                continue;
            }
            final ArrayList<NmsModelEntityConfig> partList = new ArrayList<>();
            NmsModelEntityConfig main = null;
            for (CustomModel.CustomEntity part : model.entities) {
                final NmsModelEntityConfig piece = new NmsModelEntityConfig(part);
                if (piece.isMain()){
                    main = piece;
                }
                else partList.add(piece);
            }
            if (main == null) {
                NmsMobsPlugin.get().log(Level.WARNING, name.getFile() + " has an invalid schematic");
            } else {
                partList.trimToSize();
                ALL_PARTS.put(name, new NmsModelConfig(main, partList));
            }
        }
    }

    // list the parts
    public static NmsModelConfig parts(ModelConfigName name) {
        return ALL_PARTS.get(name);
    }

    public NmsModelEntityConfig mainPart() {
        return this.mainConfig;
    }

    public List<NmsModelEntityConfig> others() {
        return this.partsConfig;
    }

    public enum ModelConfigName {
        WARPED_GREMLIN("warped_gremlin"), ALEDAR_CART("aledar_cart");

        private final String name;

        ModelConfigName(String name) {
            this.name = name;
        }

        public @NotNull String getFile() {
            return name;
        }
    }
}
