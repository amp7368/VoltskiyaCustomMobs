package apple.voltskiya.custom_mobs.mobs.nms.parts;

import apple.voltskiya.custom_mobs.trash.dungeon.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.trash.dungeon.custom_model.handling.CustomModelEntityConfig;

public class NmsModelEntityConfig extends CustomModelEntityConfig {
    private final boolean isMain;

    public NmsModelEntityConfig(CustomModelDataEntity entity) {
        super(entity);
        // do stuff with entity.otherData if necessary
        final Object isMain = entity.otherData.get("isMain");
        this.isMain = isMain instanceof Boolean && (Boolean) isMain;
    }

    public boolean isMain() {
        return isMain;
    }
}
