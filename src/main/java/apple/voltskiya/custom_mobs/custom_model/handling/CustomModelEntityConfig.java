package apple.voltskiya.custom_mobs.custom_model.handling;

import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;

public class CustomModelEntityConfig {
    protected final CustomModelDataEntity data;

    public CustomModelEntityConfig(CustomModelDataEntity entity) {
        this.data = entity;
    }

    public CustomModelDataEntity getData() {
        return data;
    }

}
