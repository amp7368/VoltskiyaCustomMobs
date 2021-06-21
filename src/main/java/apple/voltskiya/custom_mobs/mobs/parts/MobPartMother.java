package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.util.EntityLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;

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


    public static List<MobPartChild> getChildren(UUID uniqueId, Entity mainEntity, NmsModelEntityConfig selfModel, NmsModelConfig.ModelConfigName modelConfig) {
        List<MobPartChild> children = new ArrayList<>();
        EntityLocation motherLocation = new EntityLocation(
                uniqueId,
                selfModel.getEntity().x,
                selfModel.getEntity().y,
                selfModel.getEntity().z,
                selfModel.getEntity().facingX,
                selfModel.getEntity().facingY,
                selfModel.getEntity().facingZ
        ); // for simpler rotations
        MobPartMother motherMe = new MobPartMother(motherLocation, mainEntity, modelConfig.getFile());
        final NmsModelConfig model = NmsModelConfig.parts(modelConfig);
        for (NmsModelEntityConfig part : model.others()) {
            children.add(MobParts.spawnMobPart(motherMe, part));
        }
        return children;
    }
}
