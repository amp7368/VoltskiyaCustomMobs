package apple.voltskiya.custom_mobs.custom_model;

import java.util.ArrayList;
import java.util.List;

public class CustomModelData {
    public List<CustomModelDataEntity> entities = new ArrayList<>();

    public void add(CustomModelDataEntity customEntity) {
        this.entities.add(customEntity);
    }

    public void adjust(double x, double y, double z) {
        for (CustomModelDataEntity e : entities) {
            e.x += x;
            e.y += y;
            e.z += z;
        }
    }
}
