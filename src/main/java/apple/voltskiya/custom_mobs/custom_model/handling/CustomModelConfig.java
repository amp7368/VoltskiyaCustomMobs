package apple.voltskiya.custom_mobs.custom_model.handling;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomModelConfig<Entity extends CustomModelEntityConfig> {
    protected final List<Entity> partsConfig = new ArrayList<>();

    public abstract void addPiece(Entity part);

    public void addPart(Entity part) {
        partsConfig.add(part);
    }

    public abstract boolean validate();

    public List<Entity> getParts() {
        return partsConfig;
    }
}
