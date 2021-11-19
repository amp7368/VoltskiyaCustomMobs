package apple.voltskiya.custom_mobs.mobs.nms.parts;

import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelConfig;

import java.util.List;

public class NmsModel extends CustomModelConfig<NmsModelEntityConfig> {
    private NmsModelEntityConfig mainConfig;

    public NmsModel() {
    }

    public NmsModelEntityConfig mainPart() {
        return this.mainConfig;
    }

    public List<NmsModelEntityConfig> others() {
        return this.partsConfig;
    }

    @Override
    public void addPiece(NmsModelEntityConfig part) {
        if (part.isMain()) {
            mainConfig = part;
        } else addPart(part);
    }

    @Override
    public boolean validate() {
        return mainConfig != null;
    }
}
