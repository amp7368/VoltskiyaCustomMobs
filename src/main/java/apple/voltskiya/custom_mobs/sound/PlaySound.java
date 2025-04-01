package apple.voltskiya.custom_mobs.sound;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlaySound {

    public SoundCategory category;
    public String soundKey;
    public float volume;
    public float pitch;
    protected transient Sound sound;

    public PlaySound() {
    }

    public PlaySound(SoundCategory category, Sound sound, float volume, float pitch) {
        this.category = category;
        this.sound = sound;
        this.soundKey = Registry.SOUND_EVENT.getKeyOrThrow(sound).asString();
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        if (sound == null) {
            NamespacedKey key = NamespacedKey.fromString(soundKey);
            if (key == null) throw new IllegalArgumentException("Sound key " + soundKey + " cannot be null");
            return Registry.SOUND_EVENT.get(key);
        }
        return sound;
    }

    public void play(Location location) {
        location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
    }

    public void play(Player player) {
        player.playSound(player, this.sound, this.category, this.volume, this.pitch);
    }
}
