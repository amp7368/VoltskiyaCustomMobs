package apple.voltskiya.custom_mobs.turret.manage;

import apple.utilities.json.gson.GsonTypeAdapterMapper;
import apple.utilities.json.gson.GsonTypeAdapterSerialization;
import apple.voltskiya.custom_mobs.turret.gm.TurretMobGm;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.player.TurretMobPlayer;

public enum TurretTypeIdentifier implements GsonTypeAdapterSerialization<TurretMob> {
    GM("typeTurretGM", TurretMobGm.class, "spawn_turret_gm"),
    PLAYER("typeTurretPlayer", TurretMobPlayer.class, "spawn_turret_player");

    private final String typeId;
    private final Class<? extends TurretMob<?>> typeClass;
    private final String spawnTag;

    TurretTypeIdentifier(String typeId, Class<? extends TurretMob<?>> typeClass, String spawnTag) {
        this.typeId = typeId;
        this.typeClass = typeClass;
        this.spawnTag = spawnTag;
    }

    public static GsonTypeAdapterMapper<TurretMob, TurretTypeIdentifier> createTypeMapper() {
        return GsonTypeAdapterMapper.create(values(), TurretMob.class);
    }

    @Override
    public String getTypeId() {
        return typeId;
    }

    @Override
    public Class<? extends TurretMob<?>> getTypeClass() {
        return typeClass;
    }

    public String getSpawnTag() {
        return spawnTag;
    }
}
