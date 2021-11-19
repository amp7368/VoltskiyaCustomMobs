package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.mob.MobAPC33Whole;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConstructor;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityRavager;
import org.jetbrains.annotations.Nullable;
import ycm.yml.manager.fields.YcmField;

import java.util.function.Consumer;

public class MobAPC33ConfigWhole extends MobAPC33Config<MobAPC33Whole, MobAPC33ConfigWhole> {
    @YcmField
    public double range = 30;
    @YcmField
    public int cooldown = 100;
    @YcmField
    public int minRange = 5;
    @YcmField
    public int cannonInterval = 40;
    @YcmField
    public int machineGunInterval = 10;
    @YcmField
    public double maxHeadRotationPerTick = 1d;

    @Override
    public String getTag() {
        return "apc33.mob";
    }

    @Override
    @Nullable
    public NmsModelHandler.ModelConfigName getModelConfigName() {
        return NmsModelHandler.ModelConfigName.APC33_MOB;
    }

    @Override
    public NmsMobConstructor<MobAPC33Whole, MobAPC33ConfigWhole> getEntityBuilder() {
        return MobAPC33Whole::new;
    }

    @Override
    public EntityTypes<EntityRavager> getReplacement() {
        return DecodeEntityTypes.RAVAGER;
    }

    @Override
    public Consumer<NmsMobRegister<MobAPC33Whole, MobAPC33ConfigWhole>> getRegisterPointer() {
        return MobAPC33Whole::setRegister;
    }

    @Override
    public MobAPC33ConfigWhole getSelf() {
        return this;
    }
}
