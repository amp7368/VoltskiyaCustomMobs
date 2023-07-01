package apple.voltskiya.custom_mobs.abilities.nether.charger;

import apple.nms.decoding.entity.DecodeEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.minecraft.TagConstants;

public class Charger {

    private final Mob charger;
    private final ChargerType type;
    private final net.minecraft.world.entity.Mob chargerHandle;
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
            !charger.getScoreboardTags().contains(TagConstants.IS_DOING_ABILITY) &&
            DecodeEntity.getTicksLived(chargerHandle) - lastCharged > type.getChargeCooldown();
    }

    public ChargerType getType() {
        return type;
    }

    public void chargeNow() {
        this.charger.addScoreboardTag(TagConstants.IS_DOING_ABILITY);
        this.lastCharged = DecodeEntity.getTicksLived(chargerHandle);
    }
}
