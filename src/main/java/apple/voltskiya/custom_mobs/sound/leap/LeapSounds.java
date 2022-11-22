package apple.voltskiya.custom_mobs.sound.leap;

import apple.voltskiya.custom_mobs.sound.PlaySound;
import apple.voltskiya.custom_mobs.sound.PlaySounds;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class LeapSounds {

    public PlaySound chargeUpGrowl = new PlaySound(SoundCategory.HOSTILE, Sound.ENTITY_WOLF_GROWL, .5f, .75f);

    public LeapSounds() {
        PlaySounds.leaps = this;
    }
}
