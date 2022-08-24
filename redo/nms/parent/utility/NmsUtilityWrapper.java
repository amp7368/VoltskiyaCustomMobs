package apple.voltskiya.custom_mobs.mobs.nms.parent.utility;

import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;

import java.util.Objects;

public class NmsUtilityWrapper<SelfEntity extends Entity> implements NmsUtility<SelfEntity> {
    private final SelfEntity selfEntity;
    private NmsMobEntitySupers supers;

    public NmsUtilityWrapper(SelfEntity selfEntity) {
        this.selfEntity = selfEntity;
    }

    @Override
    public NmsMobEntitySupers getEntitySupers() {
        return this.supers = Objects.requireNonNullElseGet(this.supers, this::makeEntitySupers);
    }

    @Override
    public NmsMobEntitySupers makeEntitySupers() {
        return new NmsMobEntitySupers(
                selfEntity::b, // change world
                selfEntity::a, // move
                selfEntity::g, // load
                selfEntity::f, // save
                selfEntity::a // die
        );
    }

    @Override
    public String getSaveId() {
        return DecodeEntityTypes.getKey(getEntityType()).toString();
    }

    @Override
    public SelfEntity getSelfEntity() {
        return selfEntity;
    }

    @Override
    public EntityTypes<?> nmsgetEntityType() {
        return selfEntity.ad();
    }

    @Override
    public AttributeMapBase nmsgetAttributeMap() {
        @SuppressWarnings("unchecked") EntityTypes<? extends EntityLiving> entityTypes = (EntityTypes<? extends EntityLiving>) this.getEntityType();
        return new AttributeMapBase(AttributeDefaults.a(entityTypes));
    }
}
