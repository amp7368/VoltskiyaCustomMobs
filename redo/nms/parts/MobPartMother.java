package apple.voltskiya.custom_mobs.mobs.nms.parts;

import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobParts;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import voltskiya.apple.utilities.EntityLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobPartMother {
    public final Location location;
    public final EntityLocation entityLocation;
    public final Entity entity;
    public final String scoreboardTag;

    public MobPartMother(EntityLocation entityLocation, Entity entity, String scoreboardTag) {
        this.entityLocation = entityLocation;
        this.entity = entity;
        this.location = new Location(null, entityLocation.x, entityLocation.y, entityLocation.z);
        this.scoreboardTag = scoreboardTag;
    }


    public static List<MobPartChild> makeChildren(UUID uniqueId, Entity mainEntity, NmsModelEntityConfig selfModel, NmsModelHandler.ModelConfigName modelConfig) {
        List<MobPartChild> children = new ArrayList<>();
        EntityLocation motherLocation = new EntityLocation(
                uniqueId,
                selfModel.getData().x,
                selfModel.getData().y,
                selfModel.getData().z,
                selfModel.getData().facingX,
                selfModel.getData().facingY,
                selfModel.getData().facingZ
        ); // for simpler rotations
        MobPartMother motherMe = new MobPartMother(motherLocation, mainEntity, modelConfig.getTag());
        final NmsModel model = NmsModelHandler.parts(modelConfig);
        for (NmsModelEntityConfig part : model.others()) {
            children.add(MobParts.spawnMobPart(motherMe, part));
        }
        return children;
    }
}
