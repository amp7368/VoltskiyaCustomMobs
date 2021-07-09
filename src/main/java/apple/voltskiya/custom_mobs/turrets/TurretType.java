package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.turrets.mobs.TurretMob;
import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobInfinite;
import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobPlayer;
import co.aikar.commands.BukkitCommandCompletionContext;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public enum TurretType {
    PLAYER("player", TurretMobPlayer::new, TurretMobPlayer.TurretMobPlayerSaveable.class),
    INFINITE("infinite", TurretMobInfinite::new, TurretMobInfinite.TurretMobInfiniteSaveable.class);
//    GM("gm", TurretGM::new)

    private final String username;
    private final Function<Player, TurretMob> builder;
    private final Class<? extends TurretMobSaveable> type;

    TurretType(String username, Function<Player, TurretMob> builder, Class<? extends TurretMobSaveable> type) {
        this.username = username;
        this.builder = builder;
        this.type = type;
    }

    public static Collection<String> typeNames(BukkitCommandCompletionContext completionContext) {
        List<String> names = new ArrayList<>();
        for (TurretType type : values()) {
            names.add(type.username);
        }
        return names;
    }

    public static TurretType getType(String name) {
        for (TurretType turretType : values()) {
            if (turretType.username.equalsIgnoreCase(name)) return turretType;
        }
        return null;
    }

    public TurretMob builder(Player player) {
        return builder.apply(player);
    }

    public String getUsername() {
        return username;
    }

    public Class<? extends TurretMobSaveable> type() {
        return type;
    }
}
