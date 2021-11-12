package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob.MobAPC33SmallGun;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConstructor;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MobAPC33ConfigSmallGun extends MobAPC33Config<MobAPC33SmallGun, MobAPC33ConfigSmallGun> {
    @Override
    public String getTag() {
        return "apc33.gun";
    }

    @Override
    public NmsModelConfig.@Nullable ModelConfigName getModelConfigName() {
        return NmsModelConfig.ModelConfigName.APC33_GUN;
    }

    @Override
    public NmsMobConstructor<MobAPC33SmallGun, MobAPC33ConfigSmallGun> getEntityBuilder() {
        return MobAPC33SmallGun::new;
    }

    @Override
    public EntityTypes<?> getReplacement() {
        return DecodeEntityTypes.ZOMBIE;
    }

    @Override
    public Consumer<NmsMobRegister<MobAPC33SmallGun, MobAPC33ConfigSmallGun>> getRegisterPointer() {
        return MobAPC33SmallGun::setRegister;
    }

    @Override
    public MobAPC33ConfigSmallGun getSelf() {
        return this;
    }

    @Override
    public boolean isSpawnable() {
        return false;
    }
}
