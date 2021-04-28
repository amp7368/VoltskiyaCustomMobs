package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.mobs.utils.NbtConstants;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;

public class NmsModelEntityConfig {
    private final CustomModel.CustomEntity entity;
    private final boolean isMain;

    public NmsModelEntityConfig(CustomModel.CustomEntity entity) {
        this.entity = entity;
        // do stuff with entity.otherData if necessary
        final Object isMain = entity.otherData.get("isMain");
        this.isMain = isMain instanceof Boolean && (Boolean) isMain;
    }

    public NmsModelEntityConfig(NBTTagCompound nbt) {
        this.entity = new CustomModel.CustomEntity(nbt.getCompound(NbtConstants.EntityLocationRelative.RELATIVE_LOCATION));
        this.isMain = nbt.getBoolean(NbtConstants.EntityLocationRelative.IS_MAIN);
    }

    // getters
    public CustomModel.CustomEntity getEntity() {
        return entity;
    }

    public boolean isMain() {
        return isMain;
    }

    public NBTBase toNbt() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean(NbtConstants.EntityLocationRelative.IS_MAIN, isMain);
        nbt.set(NbtConstants.EntityLocationRelative.RELATIVE_LOCATION, entity.toNbt());
        return nbt;
    }
}
