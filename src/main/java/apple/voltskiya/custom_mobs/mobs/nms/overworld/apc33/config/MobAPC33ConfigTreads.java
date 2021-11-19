package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob.MobAPC33Treads;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConstructor;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MobAPC33ConfigTreads extends MobAPC33Config<MobAPC33Treads, MobAPC33ConfigTreads> {
    @Override
    public String getTag() {
        return "apc33.treads";
    }

    @Override
    public NmsModelHandler.@Nullable ModelConfigName getModelConfigName() {
        return NmsModelHandler.ModelConfigName.APC33_TREADS;
    }

    @Override
    public NmsMobConstructor<MobAPC33Treads, MobAPC33ConfigTreads> getEntityBuilder() {
        return MobAPC33Treads::new;
    }

    @Override
    public EntityTypes<?> getReplacement() {
        return DecodeEntityTypes.ZOMBIE;
    }

    @Override
    public Consumer<NmsMobRegister<MobAPC33Treads, MobAPC33ConfigTreads>> getRegisterPointer() {
        return MobAPC33Treads::setRegister;
    }

    @Override
    public MobAPC33ConfigTreads getSelf() {
        return this;
    }

    @Override
    public boolean isSpawnable() {
        return false;
    }
}
