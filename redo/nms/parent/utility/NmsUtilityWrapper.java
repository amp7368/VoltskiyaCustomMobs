package apple.voltskiya.custom_mobs.nms.parent.utility;

import apple.nms.decoding.iregistry.DecodeEntityType;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

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
        return DecodeEntityType.getKey(getEntityType()).toString();
    }

    @Override
    public SelfEntity getSelfEntity() {
        return selfEntity;
    }

    @Override
    public EntityType<?> nmsgetEntityType() {
        return selfEntity.ad();
    }

    @Override
    public AttributeMap nmsgetAttributeMap() {
        @SuppressWarnings("unchecked") EntityType<? extends EntityLiving> EntityType = (EntityType<? extends EntityLiving>) this.getEntityType();
        return new AttributeMap(DefaultAttributes.a(EntityType));
    }
}
