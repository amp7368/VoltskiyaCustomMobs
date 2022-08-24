package apple.voltskiya.custom_mobs.mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class NmsMobWrapperQOL<SelfEntity extends Entity & NmsUtility<SelfEntity>> {
    private final SelfEntity selfEntity;
    private final Random random = new Random();
    private AttributeMapBase attributeMap = null;
    private NmsMobEntitySupers entitySupers;

    public NmsMobWrapperQOL(SelfEntity entity) {
        this.selfEntity = entity;
    }

    public AttributeMapBase getAttributeMap() {
        return this.attributeMap = Objects.requireNonNullElseGet(this.attributeMap, this::makeAttributeMap);
    }

    @NotNull
    private AttributeMapBase makeAttributeMap() {
        return new AttributeMapBase(selfEntity.getAttributeProvider());
    }

    public SelfEntity getSelfEntity() {
        return selfEntity;
    }

    public Random getRandom() {
        return random;
    }

    public NmsMobEntitySupers getEntitySupers() {
        return entitySupers = Objects.requireNonNullElseGet(entitySupers, selfEntity::makeEntitySupers);
    }
}
