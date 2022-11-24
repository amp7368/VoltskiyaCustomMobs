package apple.voltskiya.custom_mobs.leap.parent.targeting;

import apple.utilities.gson.adapter.GsonEnumTypeAdapter;
import apple.utilities.gson.adapter.GsonEnumTypeHolder;
import apple.utilities.json.gson.GsonBuilderDynamic;

public enum TargetingConfigType implements GsonEnumTypeHolder<TargetingConfig> {
    FOLLOWING("following", TargetingConfigFollowing.class),
    PEAKS("peaks", TargetingConfigPeaks.class),
    RANDOM("random", TargetingConfigRandom.class);


    private final String typeId;
    private final Class<? extends TargetingConfig> typeClass;

    TargetingConfigType(String typeId, Class<? extends TargetingConfig> typeClass) {
        this.typeId = typeId;
        this.typeClass = typeClass;
    }

    public static GsonBuilderDynamic register(GsonBuilderDynamic gson) {
        return GsonEnumTypeAdapter.register(values(), gson, TargetingConfig.class);
    }

    @Override
    public String getTypeId() {
        return this.typeId;
    }

    @Override
    public Class<? extends TargetingConfig> getTypeClass() {
        return this.typeClass;
    }
}
