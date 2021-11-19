package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NmsMobHolder<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>> {
    private final NmsMobRegister<TypeEntity, Config> register;
    private final TypeEntity mob;
    private List<MobPartChild> children;
    private AttributeMapBase attributeMap;

    public NmsMobHolder(NmsMobRegister<TypeEntity, Config> register, TypeEntity mob) {
        this.register = register;
        this.mob = mob;
    }

    public void addChildren(UUID uuid, NmsModel model, NmsModelHandler.ModelConfigName modelName, Entity mainEntity) {
        killParts();
        this.children = MobPartMother.getChildren(uuid, mainEntity, model.mainPart(), modelName);
    }

    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(register.getAttributeProvider()) : this.attributeMap;
    }

    public List<Packet<?>> move(boolean isLookingRelevant) {
        return move(child -> child.moveFromMother(isLookingRelevant));
    }

    public List<Packet<?>> move(Function<MobPartChild, Packet<?>> childMover) {
        List<Packet<?>> packetsToSend = new ArrayList<>();
        for (MobPartChild child : children) {
            packetsToSend.add(childMover.apply(child));
        }
        return packetsToSend;
    }

    public List<MobPartChild> getChildren() {
        return this.children;
    }

    public NmsMobRegister<TypeEntity, Config> getRegister() {
        return this.register;
    }

    public void killParts() {
        if (this.children != null) {
            for (MobPartChild child : children) {
                child.die();
            }
        }
    }

    public TypeEntity getEntity() {
        return mob;
    }

    public boolean hasModel() {
        return register.hasModel();
    }

    public Config getConfig() {
        return register.getConfig();
    }
}
