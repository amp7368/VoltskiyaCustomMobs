package apple.voltskiya.custom_mobs.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record PlayerClose(@Nullable Player player, double distance) {
    @Nullable
    public Location getLocation() {
        return player == null ? null : player.getLocation();
    }
}
