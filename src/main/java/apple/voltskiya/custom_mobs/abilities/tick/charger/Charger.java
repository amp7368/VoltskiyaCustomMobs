package apple.voltskiya.custom_mobs.abilities.tick.charger;

import apple.voltskiya.custom_mobs.util.constants.TagConstants;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

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
                chargerHandle.ticksLived - lastCharged > type.getChargeCooldown();
    }

    public ChargerType getType() {
        return type;
    }

    public void chargeNow() {
        this.charger.addScoreboardTag(TagConstants.isDoingAbility);
        this.lastCharged = chargerHandle.ticksLived;
    }
}
