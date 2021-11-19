package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob.MobAPC33LargeCannon;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConstructor;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MobAPC33ConfigLargeCannon extends MobAPC33Config<MobAPC33LargeCannon, MobAPC33ConfigLargeCannon> {
    @Override
    public String getTag() {
        return "apc33.cannon";
    }

    @Override
    public NmsModelHandler.@Nullable ModelConfigName getModelConfigName() {
        return NmsModelHandler.ModelConfigName.APC33_CANNON;
    }

    @Override
    public NmsMobConstructor<MobAPC33LargeCannon, MobAPC33ConfigLargeCannon> getEntityBuilder() {
        return MobAPC33LargeCannon::new;
    }

    @Override
    public EntityTypes<?> getReplacement() {
        return DecodeEntityTypes.ZOMBIE;
    }

    @Override
    public Consumer<NmsMobRegister<MobAPC33LargeCannon, MobAPC33ConfigLargeCannon>> getRegisterPointer() {
        return MobAPC33LargeCannon::setRegister;
    }

    @Override
    public MobAPC33ConfigLargeCannon getSelf() {
        return this;
    }

    @Override
    public boolean isSpawnable() {
        return false;
    }
}
