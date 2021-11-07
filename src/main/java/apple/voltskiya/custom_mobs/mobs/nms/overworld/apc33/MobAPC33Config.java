package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33;

import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.IOException;
import java.util.function.BiFunction;

public class MobAPC33Config extends ConfigManager {
    public MobAPC33Config() throws IOException {
        MobAPCMachineGunType.NORMAL.minRange = (double) getValueOrInit(YmlSettings.NORMAL_MIN_RANGE.getPath());
        MobAPCMachineGunType.NORMAL.range = (double) getValueOrInit(YmlSettings.NORMAL_RANGE.getPath());
        MobAPCMachineGunType.NORMAL.cooldown = (int) getValueOrInit(YmlSettings.NORMAL_COOLDOWN.getPath());
        MobAPCMachineGunType.NORMAL.machine_interval = (int) getValueOrInit(YmlSettings.NORMAL_MACHINE_INTERVAL.getPath());
        MobAPCMachineGunType.NORMAL.cannonInterval = (int) getValueOrInit(YmlSettings.NORMAL_CANNON_INTERVAL.getPath());
    }

    @Override
    public String getName() {
        return "apc33";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    protected PluginManagedModule getPlugin() {
        return PluginNmsMobs.get();
    }

    public enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
        NORMAL_RANGE("normal.range", 13d),
        NORMAL_COOLDOWN("normal.cooldown", 300),
        NORMAL_MIN_RANGE("normal.min_range", 4d),
        NORMAL_CANNON_INTERVAL("normal.cannon_interval", 40),
        NORMAL_MACHINE_INTERVAL("normal.machine_gun_interval", 10);
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

    public enum MobAPCMachineGunType implements PathfinderGoalShootSpell.SpellType<MobAPC33SpellCaster> {
        NORMAL(0, 0, APC33GunSpell::new, 0, 0);

        private final BiFunction<MobAPC33SpellCaster, MobAPCMachineGunType, PathfinderGoalShootSpell.Spell> runnableConstructor;
        public double minRange;
        private double range;
        private int cooldown;
        private long machine_interval;
        private int cannonInterval;

        MobAPCMachineGunType(double range, int cooldown, BiFunction<MobAPC33SpellCaster, MobAPCMachineGunType, PathfinderGoalShootSpell.Spell> runnableConstructor, long interval, int cannonInterval) {
            this.range = range;
            this.cooldown = cooldown;
            this.runnableConstructor = runnableConstructor;
            this.machine_interval = interval;
            this.cannonInterval = cannonInterval;
        }

        @Override
        public int getCooldown() {
            return cooldown;
        }

        @Override
        public boolean inRange(double distance) {
            return distance <= range;
        }

        @Override
        public PathfinderGoalShootSpell.Spell construct(MobAPC33SpellCaster me) {
            return runnableConstructor.apply(me, this);
        }

        public long getMachineGunInterval() {
            return machine_interval;
        }

        public int getCannonGunInterval() {
            return cannonInterval;
        }
    }
}
