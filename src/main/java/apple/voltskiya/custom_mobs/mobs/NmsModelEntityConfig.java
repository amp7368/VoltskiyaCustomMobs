package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.custom_model.CustomModel;

public class NmsModelEntityConfig {
    private final CustomModel.CustomEntity entity;
    private final boolean isMain;

    public NmsModelEntityConfig(CustomModel.CustomEntity entity) {
        this.entity = entity;
        // do stuff with entity.otherData if necessary
        final Object isMain = entity.otherData.get("isMain");
        this.isMain = isMain instanceof Boolean && (Boolean) isMain;
    }

    // getters
    public CustomModel.CustomEntity getEntity() {
        return entity;
    }

    public boolean isMain() {
        return isMain;
    }
}
