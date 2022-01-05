package apple.voltskiya.custom_mobs.mobs.abilities.tick.charger;

import apple.nms.decoding.entity.DecodeEntity;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.constants.TagConstants;

public class Charger {
    private final Mob charger;
    private final ChargerType type;
    private final EntityInsentient chargerHandle;
    private int lastCharged;

    public Charger(@NotNull Mob charger, ChargerType type) {
        this.charger = charger;
        this.chargerHandle = ((CraftMob) charger).getHandle();
        this.type = type;
    }

    public Mob getEntity() {
        return this.charger;
    }

    public boolean isChargeable() {
        return !charger.isDead() &&
                !charger.getScoreboardTags().contains(TagConstants.isDoingAbility) &&
                DecodeEntity.getTicksLived(chargerHandle) - lastCharged > type.getChargeCooldown();
    }

    public ChargerType getType() {
        return type;
    }

    public void chargeNow() {
        this.charger.addScoreboardTag(TagConstants.isDoingAbility);
        this.lastCharged = DecodeEntity.getTicksLived(chargerHandle);
    }
}
