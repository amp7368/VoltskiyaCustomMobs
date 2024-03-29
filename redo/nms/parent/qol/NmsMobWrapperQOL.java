package apple.voltskiya.custom_mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class NmsMobWrapperQOL<SelfEntity extends Entity & NmsUtility<SelfEntity>> {
    private final SelfEntity selfEntity;
    private final Random random = new Random();
    private AttributeMap attributeMap = null;
    private NmsMobEntitySupers entitySupers;

    public NmsMobWrapperQOL(SelfEntity entity) {
        this.selfEntity = entity;
    }

    public AttributeMap getAttributeMap() {
        return this.attributeMap = Objects.requireNonNullElseGet(this.attributeMap, this::makeAttributeMap);
    }

    @NotNull
    private AttributeMap makeAttributeMap() {
        return new AttributeMap(selfEntity.getAttributeSupplier());
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
