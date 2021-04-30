package apple.voltskiya.custom_mobs.leaps.sounds;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.function.Consumer;

public class LeapSounds {
    public static Consumer<Location> CHARGE_UP_GROWL = (location -> location.getWorld().playSound(location, Sound.ENTITY_WOLF_GROWL, SoundCategory.HOSTILE, 1, .75f));
}
