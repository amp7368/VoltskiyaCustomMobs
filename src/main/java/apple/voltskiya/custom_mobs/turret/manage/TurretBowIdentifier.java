package apple.voltskiya.custom_mobs.turret.manage;

import apple.utilities.json.gson.GsonTypeAdapterMapper;
import apple.utilities.json.gson.GsonTypeAdapterSerialization;
import apple.voltskiya.custom_mobs.turret.infinite.TurretBowInfinite;
import apple.voltskiya.custom_mobs.turret.parent.TurretBow;

public enum TurretBowIdentifier implements GsonTypeAdapterSerialization<TurretBow> {
    INFINITE("turretBowInfinite", TurretBowInfinite.class),
    DEFAULT("typeBowDefault", TurretBow.class);

    private final String typeId;
    private final Class<? extends TurretBow> typeClass;

    TurretBowIdentifier(String typeId, Class<? extends TurretBow> typeClass) {
        this.typeId = typeId;
        this.typeClass = typeClass;
    }

    public static GsonTypeAdapterMapper<TurretBow, TurretBowIdentifier> createTypeMapper() {
        return GsonTypeAdapterMapper.create(values(), TurretBow.class);
    }

    @Override
    public String getTypeId() {
        return typeId;
    }

    @Override
    public Class<? extends TurretBow> getTypeClass() {
        return typeClass;
    }
}
