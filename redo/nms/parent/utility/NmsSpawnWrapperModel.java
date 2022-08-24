package apple.voltskiya.custom_mobs.mobs.nms.parent.utility;

import apple.voltskiya.custom_mobs.mobs.nms.parent.qol.NmsModelHolderQOL;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Location;

public class NmsSpawnWrapperModel<SelfEntity extends Mob & NmsModelHolderQOL<SelfEntity>> extends NmsSpawnWrapper<SelfEntity> {
    private final NmsModelHandler.ModelConfigName modelName;
    private NmsModel model;

    public NmsSpawnWrapperModel(String name, EntityTypes.b<SelfEntity> create, EntityTypes<?> replacement, NmsModelHandler.ModelConfigName registeredModel) {
        super(name, create, replacement);
        this.modelName = registeredModel;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.model = NmsModelHandler.parts(modelName);
    }

    @Override
    public SelfEntity spawn(Location location, NBTTagCompound oldNbt) {
        SelfEntity spawned = super.spawn(location, oldNbt);
        spawned.addChildrenPost();
        return null;
    }

    public NmsModel getSelfModel() {
        return model;
    }

    public NmsModelHandler.ModelConfigName getModelName() {
        return modelName;
    }
}
