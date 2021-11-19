package apple.voltskiya.custom_mobs.trash.old_turrets2;

import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMob;
import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobGM;
import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobInfinite;
import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobPlayer;
import co.aikar.commands.BukkitCommandCompletionContext;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public enum Old2TurretType {
    PLAYER("player", Old2TurretMobPlayer::new, Old2TurretMobPlayer.Old2TurretMobPlayerSaveable.class),
    INFINITE("infinite", Old2TurretMobInfinite::new, Old2TurretMobInfinite.Old2TurretMobInfiniteSaveable.class),
    GM("gm", Old2TurretMobGM::new, Old2TurretMobGM.Old2TurretMobGMSaveable.class);

    private final String username;
    private final Function<Player, Old2TurretMob> builder;
    private final Class<? extends Old2TurretMobSaveable> type;

    Old2TurretType(String username, Function<Player, Old2TurretMob> builder, Class<? extends Old2TurretMobSaveable> type) {
        this.username = username;
        this.builder = builder;
        this.type = type;
    }

    public static Collection<String> typeNames(BukkitCommandCompletionContext completionContext) {
        List<String> names = new ArrayList<>();
        for (Old2TurretType type : values()) {
            names.add(type.username);
        }
        return names;
    }

    public static Old2TurretType getType(String name) {
        for (Old2TurretType turretType : values()) {
            if (turretType.username.equalsIgnoreCase(name)) return turretType;
        }
        return null;
    }

    public Old2TurretMob builder(Player player) {
        return builder.apply(player);
    }

    public String getUsername() {
        return username;
    }

    public Class<? extends Old2TurretMobSaveable> type() {
        return type;
    }
}
