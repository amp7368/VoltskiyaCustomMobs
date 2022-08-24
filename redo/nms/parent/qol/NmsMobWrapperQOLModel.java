package apple.voltskiya.custom_mobs.mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class NmsMobWrapperQOLModel
        <SelfEntity extends Mob & NmsModelHolderQOL<SelfEntity>>
        extends NmsMobWrapperQOL<SelfEntity> {
    private List<MobPartChild> children = null;

    public NmsMobWrapperQOLModel(SelfEntity entity) {
        super(entity);
    }

    public List<MobPartChild> verifyChildren() {
        if (this.children == null)
            addChildren();
        return this.children;
    }

    public void addChildren() {
        if (this.children != null) {
            for (MobPartChild child : children) {
                child.die();
            }
        }
        SelfEntity selfEntity = this.getSelfEntity();
        NmsSpawnWrapperModel<SelfEntity> spawner = selfEntity.getSpawner();
        this.children = MobPartMother.makeChildren(selfEntity.getBukkitEntity().getUniqueId(), selfEntity, spawner.getSelfModel().mainPart(), spawner.getModelName());
        selfEntity.addChildrenPost();
    }

    public void moveChildren() {
        this.verifyChildren();
        List<Packet<?>> packetsToSend = new ArrayList<>();
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, getSelfEntity().getBukkitEntity().getLocation());
    }

    public void die() {
        for (MobPartChild child : children) {
            child.die();
        }

    }
}
