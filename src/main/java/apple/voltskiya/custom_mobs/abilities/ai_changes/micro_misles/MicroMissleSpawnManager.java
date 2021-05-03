package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.SpawnEater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vex;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class MicroMissleSpawnManager extends SpawnEater {
    public static int MIN_TICKS_TO_LIVE = 10;
    public static int ADDITIONAL_TICKS_TO_LIVE = 40;
    public static float SPEED = 0.6f;
    public static float ACCELERATION_SPEED = 0.3f;
    public static double VARIABLITY = 7;
    public static int RANDOM_ACCELERATION_ANGLE = 30;
    public double DAMAGE_AMOUNT;
    public static String SUMMON_MISSILE_VEX;
    private static MicroMissleSpawnManager instance;
    private final MicroMissileIndividualTicker ticker = new MicroMissileIndividualTicker();

    public MicroMissleSpawnManager() throws IOException {
        instance = this;
        DAMAGE_AMOUNT = (double) getValueOrInit(YmlSettings.DAMAGE_AMOUNT.getPath());
        SUMMON_MISSILE_VEX = getValueOrInit(YmlSettings.SUMMON_MISSILE_VEX.getPath()).toString();
        MIN_TICKS_TO_LIVE = (int) getValueOrInit(YmlSettings.MIN_TICKS_TO_LIVE.getPath());
        ADDITIONAL_TICKS_TO_LIVE = (int) getValueOrInit(YmlSettings.ADDITIONAL_TICKS_TO_LIVE.getPath());
        SPEED = (float) getValueOrInit(YmlSettings.SPEED.getPath());
        ACCELERATION_SPEED = (float) getValueOrInit(YmlSettings.ACCELERATION_SPEED.getPath());
        VARIABLITY = (double) getValueOrInit(YmlSettings.VARIABILITY.getPath());
        RANDOM_ACCELERATION_ANGLE = (int) getValueOrInit(YmlSettings.RANDOM_ACCELERATION_ANGLE.getPath());
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (!(striker instanceof Vex)) continue;
            ticker.giveVex((Vex) striker);
        }
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.VEX) {
            // this is a vex
            final Vex vex = (Vex) event.getEntity();
            ticker.giveVex(vex);
            addMobs(vex.getUniqueId());
        }
    }

    public static MicroMissleSpawnManager get() {
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
        return MicroMissleSpawnManager.YmlSettings.values();
    }

    /**
     * @return the module associated with this configthe module associated with this config
     */
    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }


    private enum YmlSettings implements apple.voltskiya.custom_mobs.YmlSettings {
        DAMAGE_AMOUNT("damage_amount", 2d),
        SUMMON_MISSILE_VEX("micro_missile_shoot", "{Silent:1b,Health:1f,LifeTicks:600,Tags:[\"" + MicroMissileIndividualTicker.MICRO_MISSLE_TAG + "\"],HandItems:[{id:\"minecraft:air\",Count:1b},{}],ActiveEffects:[{Id:14b,Amplifier:1b,Duration:100000,ShowParticles:0b}],Attributes:[{Name:generic.max_health,Base:1},{Name:generic.attack_damage,Base:0}]}"),
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
