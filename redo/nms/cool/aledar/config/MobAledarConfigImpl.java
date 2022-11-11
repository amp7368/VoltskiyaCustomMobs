package apple.voltskiya.custom_mobs.nms.cool.aledar.config;

import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.cool.aledar.mob.MobAledar;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobConstructor;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobRegisterConfigable;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MobAledarConfigImpl extends MobAledarConfig<MobAledar, MobAledarConfigImpl> {

    private static final NmsModelHandler.ModelConfigName REGISTERED_MODEL = NmsModelHandler.ModelConfigName.ALEDAR_CART;

    @Override
    public String getTag() {
        return REGISTERED_MODEL.getName();
    }

    @Override
    public NmsModelHandler.@Nullable ModelConfigName getModelConfigName() {
        return REGISTERED_MODEL;
    }

    @Override
    public NmsMobConstructor<MobAledar, MobAledarConfigImpl> getEntityBuilder() {
        return MobAledar::new;
    }

    @Override
    public EntityType<?> getReplacement() {
        return DecodeEntityType.PILLAGER;
    }

    @Override
    public Consumer<NmsMobRegisterConfigable<MobAledar, MobAledarConfigImpl>> getRegisterPointer() {
        return MobAledar::setRegister;
    }

    @Override
    public MobAledarConfigImpl getSelf() {
        return this;
    }
}
