package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.IOException;

public class MicroMissileConfig extends ConfigManager {
    public static int MIN_TICKS_TO_LIVE = 17;
    public static int ADDITIONAL_TICKS_TO_LIVE = 40;
    public static float SPEED = 0.6f;
    public static float ACCELERATION_SPEED = 0.3f;
    public static double VARIABLITY = 7;
    public static int RANDOM_ACCELERATION_ANGLE = 30;
    public static double DAMAGE_AMOUNT;
    private static MicroMissileConfig instance;

    public MicroMissileConfig() throws IOException {
        instance = this;
        DAMAGE_AMOUNT = (double) getValueOrInit(YmlSettings.DAMAGE_AMOUNT.getPath());
        MIN_TICKS_TO_LIVE = (int) getValueOrInit(YmlSettings.MIN_TICKS_TO_LIVE.getPath());
        ADDITIONAL_TICKS_TO_LIVE = (int) getValueOrInit(YmlSettings.ADDITIONAL_TICKS_TO_LIVE.getPath());
        SPEED = (float) (double) getValueOrInit(YmlSettings.SPEED.getPath());
        ACCELERATION_SPEED = (float) (double) getValueOrInit(YmlSettings.ACCELERATION_SPEED.getPath());
        VARIABLITY = (double) getValueOrInit(YmlSettings.VARIABILITY.getPath());
        RANDOM_ACCELERATION_ANGLE = (int) getValueOrInit(YmlSettings.RANDOM_ACCELERATION_ANGLE.getPath());

    }


    public static MicroMissileConfig get() {
        return instance;
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "micro_missile";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return MicroMissileConfig.YmlSettings.values();
    }

    /**
     * @return the module associated with this configthe module associated with this config
     */
    @Override
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
    }


    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings {
        DAMAGE_AMOUNT("damage_amount", 2.5d),
        MIN_TICKS_TO_LIVE("min_ticks_to_live", 10),
        ADDITIONAL_TICKS_TO_LIVE("additional_ticks_to_live", 40),
        SPEED("missile_speed", 0.6f),
        ACCELERATION_SPEED("missile_random_acceleration", 0.3f),
        VARIABILITY("variability", 7d),
        RANDOM_ACCELERATION_ANGLE("random_acceleration_angle", 30);

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
