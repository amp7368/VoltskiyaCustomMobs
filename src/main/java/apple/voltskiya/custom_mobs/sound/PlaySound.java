package apple.voltskiya.custom_mobs.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlaySound {

    public SoundCategory category;
    public Sound sound;
    public float volume;
    public float pitch;

    public PlaySound() {
    }

    public PlaySound(SoundCategory category, Sound sound, float volume, float pitch) {
        this.category = category;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Location location) {
        location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
    }

    public void play(Player player) {
        player.playSound(player, this.sound, this.category, this.volume, this.pitch);
    }
}
