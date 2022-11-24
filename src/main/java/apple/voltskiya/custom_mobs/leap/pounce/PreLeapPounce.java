package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.LeapStage;
import apple.voltskiya.custom_mobs.sound.PlaySounds;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;

public class PreLeapPounce<Config extends LeapPounceConfig> extends LeapStage<Config> {

    private static final AttributeModifier READY_LEAP = new AttributeModifier(UUID.randomUUID(), "ready_leap", -100,
        Operation.ADD_SCALAR);

    public PreLeapPounce(MMSpawned mob, Config config, Location targetLocation) {
        super(mob, config,config.preLeap, targetLocation);
    }

    @Override
    public void onStart() {
        applySpeedAttribute(true);
    }

    @Override
    public void onAnyFinish() {
        applySpeedAttribute(false);
    }

    private void applySpeedAttribute(boolean isAdd) {
        AttributeInstance speedAttribute = getMob().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute == null)
            return;
        if (isAdd)
            speedAttribute.addModifier(READY_LEAP);
        else
            speedAttribute.removeModifier(READY_LEAP);
    }

    @Override
    public void tick() {
        if (this.tick % 3 == 0)
            PlaySounds.leaps.chargeUpGrowl.play(getLocation());
    }
}
